package trikita.anvilgen

import com.squareup.javapoet.*
import groovy.transform.EqualsAndHashCode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier
import java.lang.reflect.Field
import java.util.jar.JarFile

class DSLGeneratorTask extends DefaultTask {

    def jarFile
    def dependencies
    def taskName
    def javadocContains
    def outputDirectory
    def outputClassName
    def packageName

    @TaskAction
    generate() {
        def attrsBuilder = TypeSpec.classBuilder(outputClassName)
                .addJavadoc("DSL for creating views and settings their attributes.\n" +
                "This file has been generated by " +
                "{@code gradle $taskName}.\n" +
                "$javadocContains.\n" +
                "Please, don't edit it manually unless for debugging.\n")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get("trikita.anvil", "BaseDSL"))

        def attrMethods = [:]

        forEachView { view ->
            processViews(attrsBuilder, view)
            forEachMethod(view) { m, name, arg, isListener ->
                attrMethods = processAttrs(attrMethods, view, m, name, arg, isListener)
            }
        }

        finalizeAttrs(attrsBuilder, attrMethods)

        JavaFile.builder(packageName, attrsBuilder.build())
                .build()
                .writeTo(getProject().file("src/$outputDirectory/java"))
    }

    def forEachView(cb) {
        def urls = [new URL("jar", "", "file:" + jarFile.getAbsolutePath() + "!/")]
        for (dep in dependencies) {
            urls.add(new URL("jar", "", "file:" + dep.getAbsolutePath() + "!/"))
        }
        def loader = new URLClassLoader(urls as URL[], getClass().getClassLoader())
        def viewClass = loader.loadClass("android.view.View")

        def jar = new JarFile(jarFile)
        for (e in Collections.list(jar.entries()).sort { it.getName() }) {
            if (e.getName().endsWith(".class")) {
                def className = e.getName().replace(".class", "").replace("/", ".")

                // Skip inner classes
                if (className.contains('$')) {
                    continue
                }
                try {
                    def c = loader.loadClass(className);
                    if (viewClass.isAssignableFrom(c) &&
                            java.lang.reflect.Modifier.isPublic(c.modifiers)) {
                        cb(c)
                    }

                } catch (NoClassDefFoundError ignored) {
                    // Simply skip this class.
                    ignored.printStackTrace()
                }
            }
        }
    }

    def forEachMethod(c, cb) {
        for (m in c.declaredMethods.sort { it.name }) {
            if (!java.lang.reflect.Modifier.isPublic(m.modifiers) ||
                    m.isSynthetic() || m.isBridge()) {
                continue
            }

            if (m.name.matches('^setOn.*Listener$')) {
                def parameterType = m.parameterTypes[0]
                if (!java.lang.reflect.Modifier.isPublic(parameterType.modifiers)) {
                    // If the parameter is not public then the method is inaccessible for us.
                    continue
                }

                def name = m.name
                cb(m, "on" + name.substring(5, name.length() - 8), parameterType, true)
            } else if (m.name.startsWith('set') && m.parameterCount == 1) {
                def parameterType = m.parameterTypes[0]
                if (!java.lang.reflect.Modifier.isPublic(parameterType.modifiers)) {
                    // If the parameter is not public then the method is inaccessible for us.
                    continue
                }

                def name = Character.toLowerCase(m.name.charAt(3)).toString() +
                        m.name.substring(4)
                cb(m, name, parameterType, false)
            }
        }
    }

    //
    // Views generator functions:
    // For each view generates a function that calls v(C), where C is a view
    // class, e.g. FrameLayout.class => frameLayout() { v(FrameLayout.class); }
    //
    def processViews(builder, view) {
        def className = view.canonicalName;
        def name = view.simpleName
        if (project.anvilgen.quirks[className]) {
            def alias = project.anvilgen.quirks[className]["__viewAlias"];
            // if the whole view class is banned - do nothing
            if (alias == false) {
                return
            } else if (alias != null) {
                name = alias
            }
        }
        name = toCase(name, { c -> Character.toLowerCase(c) })
        builder.addMethod(MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("trikita.anvil", outputClassName, "ViewClassResult"))
                .addStatement("return v(\$T.class)", view)
                .build())
        builder.addMethod(MethodSpec.methodBuilder(name)
                .addParameter(ParameterSpec.builder(ClassName.get("trikita.anvil",
                "Anvil", "Renderable"), "r").build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.BOXED_VOID)
                .addStatement("return v(\$T.class, r)", view)
                .build())
    }

    //
    // Attrs generator functions
    //
    def processAttrs(methods, view, m, name, arg, isListener) {
        def fn
        def key = new MethodKey(name, arg)
        if (isListener) {
            fn = listener(methods.get(key), m)
        } else {
            fn = setter(methods.get(key), m)
        }
        if (fn) {
            methods.put(key, fn)
        }
        return methods
    }

    def finalizeAttrs(builder, methods) {
        methods.sort { it.key.method + " " + it.key.cls.name }.each {
            def cls = TypeName.get(it.key.cls).box()
            if (cls.isPrimitive()) {
                cls = c.box()
            }
            def attrFuncType = ClassName.get("trikita.anvil", "Anvil", "AttrFunc")
            def className = toCase(it.key.method, { c -> Character.toUpperCase(c) }) +
                    "Func" + Integer.toHexString(cls.hashCode())
            def viewParam =
                    ParameterSpec.builder(ClassName.get("android.view", "View"), "v")
                            .addModifiers(Modifier.FINAL)
                            .build();
            def attrBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .addSuperinterface(ParameterizedTypeName.get(attrFuncType, cls))
            attrBuilder.addField(FieldSpec
                    .builder(ClassName.get("", className), "instance")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new ${className}()")
                    .build())
            attrBuilder.addMethod(it.value.build())
            builder.addType(attrBuilder.build())

            def wrapperMethod = MethodSpec.methodBuilder(it.key.method)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ParameterSpec.builder(it.key.cls, "arg").build())
                    .returns(TypeName.BOXED_VOID)
                    .addStatement("return ${outputClassName}.attr(${className}.instance, arg)")
            builder.addMethod(wrapperMethod.build())
        }
    }

    def attrApplyBuilder(m) {
        def cls = TypeName.get(m.getParameterTypes()[0]).box()
        return MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.view", "View"), "v")
                .addParameter(ParameterSpec.builder(cls, "arg")
                .addModifiers(Modifier.FINAL).build())
                .addParameter(ParameterSpec.builder(cls, "old")
                .addModifiers(Modifier.FINAL).build())
    }

    def listener(builder, m) {
        if (!builder) {
            builder = attrApplyBuilder(m)
        }

        def className = m.declaringClass.canonicalName;
        def listenerClass = m.getParameterTypes()[0];

        def listener = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(listenerClass)
        listenerClass.getDeclaredMethods().sort { it.getName() }.each { lm ->
            def methodBuilder = MethodSpec.methodBuilder(lm.getName())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(lm.getReturnType())

            def args = ""
            lm.getParameterTypes().eachWithIndex { v, i ->
                methodBuilder.addParameter(v, "a${i}")
                args += (i != 0 ? ", " : "") + "a${i}"
            }

            if (lm.getReturnType().equals(Void.TYPE)) {
                methodBuilder
                        .addStatement("arg.\$L(${args})", lm.getName())
                        .addStatement("\$T.render()", ClassName.get("trikita.anvil", "Anvil"))
            } else {
                methodBuilder
                        .addStatement("\$T r = arg.\$L(${args})",
                        lm.getReturnType(), lm.getName())
                        .addStatement("\$T.render()", ClassName.get("trikita.anvil", "Anvil"))
                        .addStatement("return r")
            }

            listener.addMethod(methodBuilder.build())
        }

        if (className == "android.view.View") {
            builder = attrApplyBuilder(m)
                    .beginControlFlow("if (arg != null)", m.getDeclaringClass())
                    .addStatement("v.${m.getName()}(\$L)", listener.build())
                    .nextControlFlow("else")
                    .addStatement("v.${m.getName()}(null)")
                    .endControlFlow();
            builder.locked = true;
        } else if (!builder.locked) {
            builder
                    .beginControlFlow("if (v instanceof \$T)", m.getDeclaringClass())
                    .beginControlFlow("if (arg != null)", m.getDeclaringClass())
                    .addStatement("((\$T) v).${m.getName()}(\$L)", m.getDeclaringClass(),
                    listener.build())
                    .nextControlFlow("else")
                    .addStatement("((\$T) v).${m.getName()}(null)", m.getDeclaringClass())
                    .endControlFlow()
                    .endControlFlow();
        }

        return builder
    }

    def setter(builder, m) {
        if (!builder) {
            builder = attrApplyBuilder(m)
        }

        def className = m.declaringClass.canonicalName;
        if (project.anvilgen.quirks[className]) {
            def argClass = m.getParameterTypes()[0].getCanonicalName();
            if (project.anvilgen.quirks[className]["${m.getName()}:${argClass}"]) {
                return project.anvilgen.quirks[className]["${m.getName()}:${argClass}"](builder)
            }
            if (project.anvilgen.quirks[className][m.getName()]) {
                return project.anvilgen.quirks[className][m.getName()](builder)
            }
        }

        if (className == "android.view.View") {
            builder = attrApplyBuilder(m)
                    .addStatement("v.${m.getName()}(arg)")
            builder.locked = true
        } else if (!builder.locked) {
            builder.beginControlFlow("if (v instanceof \$T)", m.getDeclaringClass())
                    .addStatement("((\$T) v).${m.getName()}(arg)", m.getDeclaringClass())
                    .endControlFlow();
        }

        return builder
    }

    def toCase(s, fn) {
        return fn(s.charAt(0)).toString() + s.substring(1)
    }

    class MethodKey {
        String method
        Class cls

        MethodKey(m, c) {
            method = m
            cls = c
        }

        @Override
        boolean equals(Object obj) {
            if (!(obj instanceof MethodKey)) {
                return false
            }
            return method.equals(obj.method) && cls.name.equals(obj.cls.name)
        }

        @Override
        int hashCode() {
            return method.hashCode() + 43 * cls.canonicalName.hashCode()
        }
    }
}

