package com.carenest.data.socket.serialization

import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageSerializer @Inject constructor() {
    
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    inline fun <reified T> decodeFromString(string: String): T? {
        return try {
            json.decodeFromString<T>(string)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    inline fun <reified T> encodeToString(value: T): String {
        return json.encodeToString(value)
    }
}
