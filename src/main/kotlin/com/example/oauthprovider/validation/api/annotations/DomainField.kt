package com.example.oauthprovider.validation.api.annotations

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainField(val required: Boolean = false)
