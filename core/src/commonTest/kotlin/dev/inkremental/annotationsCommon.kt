package dev.inkremental

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
expect annotation class Before()

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
expect annotation class After()
