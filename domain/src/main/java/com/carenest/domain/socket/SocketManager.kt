package com.carenest.domain.socket

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * High-level Socket Manager interface that is exposed to repositories.
 */
interface SocketManager : SocketConnectionManager {
    
    /**
     * Subscribes to a given STOMP topic and returns a flow of type [T].
     * Subscriptions are automatically re-established if the socket reconnects.
     */
    fun <T : Any> subscribe(topic: String, clazz: KClass<T>): Flow<T>

    /**
     * Sends a command with a strongly typed payload to the specified destination.
     * The [payload] must be serializable.
     */
    suspend fun <T : Any> send(destination: String, payload: T? = null)
    
    /**
     * Helper to send an empty command to a destination.
     */
    suspend fun sendEmpty(destination: String)
}

/**
 * Subscribes to a given STOMP topic and returns a flow of type [T].
 * Subscriptions are automatically re-established if the socket reconnects.
 * The [T] must be a reified type so the data layer knows how to deserialize it.
 */
inline fun <reified T : Any> SocketManager.subscribe(topic: String): Flow<T> =
    subscribe(topic, T::class)
