package com.example.oauthprovider.core

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainField(val required: Boolean = false)
