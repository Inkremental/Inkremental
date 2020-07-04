package dev.inkremental.meta.model

import com.squareup.kotlinpoet.*
import kotlinx.serialization.Serializable
import java.io.Serializable as JvmSerializable

// Inner Any can be String and Boolean for __viewAlias and Boolean for others
typealias InkrementalQuirks = MutableMap<String, Map<String, Any?>>
typealias InkrementalTransformers = Map<String, Map<String, List<DslTransformer>>>

enum class InkrementalType { SDK, LIBRARY }
enum class InkrementalPlatform { ANDROID, IOS }

@Serializable
sealed class DslTransformer : JvmSerializable {
    /**
     * @return True if you want this transformer to stop execution of regular dsl generator
     */
    open fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
        return false
    }
    /**
     * @return True if you want this transformer to stop execution of regular dsl generator
     */
    open fun handleTransformersForAttrSetter(builder: CodeBlock.Builder, owner: ViewModel, v: String, setterName: String, argAsParam: String, nullable : Boolean): Boolean {
        return false
    }

    @Serializable
    object FloatPixelToDipSizeTransformer : DslTransformer() {

        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            if (attrModel.type.argType.toString() == FLOAT.canonicalName) {
                with(builder) {
                    addParameter("arg", DIP_CLASS)
                    returns(UNIT)
                    addCode(CodeBlock.of("return %M(%S, arg.value)", attr, attrModel.name))
                }
                return true
            }
            return false
        }

        override fun handleTransformersForAttrSetter(builder: CodeBlock.Builder, owner: ViewModel, v: String, setterName: String, argAsParam: String, nullable : Boolean): Boolean {
            with(builder) {
                beginControlFlow("v is %T && arg is Int ->", owner.starProjectedType)
                addStatement("$v.$setterName(%M($argAsParam).toFloat())", DIP_EXT)
            }
            return true
        }

    }
    @Serializable
    data class RequiresApiTransformer(val api : Int) : DslTransformer() {
        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            builder.addAnnotation(androidx.annotation.RequiresApi::class.asTypeName()) {
                addMember("api = $api")
            }
            return false
        }
    }

    @Serializable
    object IntToDpTransformer : DslTransformer() {

        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            if (attrModel.type.argType.toString() == INT.canonicalName) {
                with(builder) {
                    addParameter("arg", DIP_CLASS)
                    returns(UNIT)
                    addCode(CodeBlock.of("return %M(%S, arg.value)", attr, attrModel.name))
                }
                return true
            }
            return false
        }

        override fun handleTransformersForAttrSetter(builder: CodeBlock.Builder, owner: ViewModel, v: String, setterName: String, argAsParam: String, nullable : Boolean): Boolean {
            with(builder) {
                beginControlFlow(" arg is Int ->", owner.starProjectedType)
                addStatement("$v.$setterName(%M($argAsParam))", DIP_EXT)
            }
            return true
        }
    }

    @Serializable
    object NullableForSureTransformer : DslTransformer() {

        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            with(builder) {
                addParameter("arg", attrModel.type.argType.copy(nullable = true))
                returns(UNIT)
                addCode(CodeBlock.of("return %M(%S, arg)", attr, attrModel.name))
            }
            return true
        }
    }

    @Serializable
    object ColorStateTransformer : DslTransformer() {

        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            if (attrModel.type.argType == COLOR_STATE_LIST) {
                val question = if (attrModel.isNullable) "?" else ""
                with(builder) {
                    addParameter("arg", COLOR_STATE.copy(nullable = attrModel.isNullable))
                    returns(UNIT)
                    addCode(CodeBlock.of("return %M(%S, arg$question.value)", attr, attrModel.name))
                }
                return true
            }
            return false
        }

        override fun handleTransformersForAttrSetter(builder: CodeBlock.Builder, owner: ViewModel, v: String, setterName: String, argAsParam: String, nullable : Boolean): Boolean {
            val question = if (nullable) "?" else ""
            with(builder) {
                beginControlFlow(" v is %T && arg is Int$question ->", owner.starProjectedType)
                if (nullable) {
                    addStatement("if(arg != null) {")
                    addStatement("  $v.$setterName(%T.valueOf(v.resources.getColor($argAsParam)))", COLOR_STATE_LIST)
                    addStatement("} else { ")
                    addStatement("  $v.$setterName(null)")
                    addStatement("}")
                } else {
                    addStatement("$v.$setterName(%T.valueOf(v.resources.getColor($argAsParam)))", COLOR_STATE_LIST)
                }
            }
            return true
        }
    }

    @Serializable
    object ColorStateCompatTransformer : DslTransformer() {

        override fun handleTransformersForDsl(builder: FunSpec.Builder, attrModel: AttrModel, attr: MemberName): Boolean {
            if (attrModel.type.argType == COLOR_STATE_LIST) {
                val question = if (attrModel.isNullable) "?" else ""
                with(builder) {
                    addParameter("arg", COLOR_STATE.copy(nullable = attrModel.isNullable))
                    returns(UNIT)
                    addCode(CodeBlock.of("return %M(%S, arg$question.value)", attr, attrModel.name))
                }
                return true
            }
            return false
        }

        override fun handleTransformersForAttrSetter(builder: CodeBlock.Builder, owner: ViewModel, v: String, setterName: String, argAsParam: String, nullable : Boolean): Boolean {
            val question = if (nullable) "?" else ""
            with(builder) {
                beginControlFlow(" v is %T && arg is Int$question ->", owner.starProjectedType)
                if (nullable) {
                    addStatement("if(arg != null) {")
                    addStatement("  $v.$setterName(%T.getColorStateList(v.resources, $argAsParam, null))", RESOURCES_COMPAT)
                    addStatement("} else { ")
                    addStatement("  $v.$setterName(null)")
                    addStatement("}")
                } else {
                    addStatement("$v.$setterName(%T.getColorStateList(v.resources, $argAsParam, null))", RESOURCES_COMPAT)
                }
            }
            return true
        }
    }
}

private val PKG_ANDROID = "$PACKAGE.android"
private val DIP_CLASS = ClassName(PKG_ANDROID, "Dip")
private val DIP_EXT = MemberName(PKG_ANDROID, "dip")
private val COLOR_STATE = ClassName(PKG_ANDROID, "ColorState")
private val COLOR_STATE_LIST = ClassName("android.content.res", "ColorStateList")
private val RESOURCES_COMPAT = ClassName("androidx.core.content.res", "ResourcesCompat")
