package com.example.oauthprovider.core

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainField(val required: Boolean = false)
