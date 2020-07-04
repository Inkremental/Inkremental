package dev.inkremental.meta.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.net.URI

internal fun <T: Any> NamedDomainObjectContainer<T>.maybeCreate(name: String, configureAction: T.() -> Unit): T =
    findByName(name) ?: create(name, configureAction)

internal fun Project.prop(key: String): String? =
    findProperty(key)?.let { it as String }

internal fun Project.envOrProp(key: String): String? =
    System.getenv(key).takeUnless(String::isNullOrEmpty) ?: prop(key)

internal fun Project.bintrayUri(
    organization: String,
    repository: String,
    packageName: String,
    publish: Boolean? = null,
    override: Boolean? = null
): URI = uri(buildString {
    append("https://api.bintray.com/maven/")
    append(organization)
    append('/')
    append(repository)
    append('/')
    append(packageName)
    append('/')
    if(publish != null) {
        append(";publish=")
        append(if(publish) '1' else '0')
    }
    if(override != null) {
        append(";override=")
        append(if(override) '1' else '0')
    }
})
