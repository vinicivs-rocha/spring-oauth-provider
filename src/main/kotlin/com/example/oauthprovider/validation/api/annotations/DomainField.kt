package com.example.oauthprovider.validation.api.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainField(val required: Boolean = false)
