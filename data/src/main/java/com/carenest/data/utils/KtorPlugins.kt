package com.carenest.data.utils

import com.carenest.data.BuildConfig
import io.ktor.client.plugins.api.createClientPlugin


/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 16/07/2026
 */


enum class KtorPluginKeys(val key: String){
    AUTHENTICATION("authentication"),
}


val authenticationplugin = createClientPlugin(KtorPluginKeys.AUTHENTICATION.key){
    onRequest { request, _ ->
        request.url {
            parameters.append(
                "key", BuildConfig.api_key
            )
        }
    }
}