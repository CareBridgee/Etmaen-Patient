package com.carenest.data.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymobHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SocketHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SocketOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationIQHttpClient