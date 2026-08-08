package com.carenest.data.socket.managers

import com.carenest.data.socket.stomp.StompClient
import com.carenest.data.socket.stomp.StompClientEvent
import com.carenest.data.socket.stomp.StompFrame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRegistry @Inject constructor(
    private val stompClient: StompClient,
) {
    private val subIdGenerator = AtomicInteger(0)

    // Map of topic to Subscription ID
    private val activeSubscriptions = ConcurrentHashMap<String, String>()

    // Flow of raw messages per subscription ID
    private val messageFlows = ConcurrentHashMap<String, MutableSharedFlow<String>>()

    private var registryScope: CoroutineScope? = null

    fun init(scope: CoroutineScope) {
        registryScope = scope
        scope.launch {
            stompClient.events.collect { event ->
                if (event is StompClientEvent.Message) {
                    val frame = event.frame
                    if (frame.command == StompFrame.MESSAGE) {
                        val subId = frame.headers["subscription"]
                        val body = frame.body
                        if (subId != null && body != null) {
                            messageFlows[subId]?.tryEmit(body)
                        }
                    } else if (frame.command == StompFrame.CONNECTED) {
                        // Resubscribe all active topics on reconnect
                        resubscribeAll()
                    }
                }
            }
        }
    }

    fun subscribe(topic: String): Flow<String> {
        val subId = activeSubscriptions.getOrPut(topic) {
            val newSubId = "sub-${subIdGenerator.incrementAndGet()}"
            messageFlows[newSubId] = MutableSharedFlow(extraBufferCapacity = 64)
            sendSubscribeFrame(topic, newSubId)
            newSubId
        }

        return messageFlows[subId] ?: MutableSharedFlow<String>(extraBufferCapacity = 64).also {
            messageFlows[subId] = it
        }
    }

    fun unsubscribe(topic: String) {
        val subId = activeSubscriptions.remove(topic)
        if (subId != null) {
            messageFlows.remove(subId)
            sendUnsubscribeFrame(subId)
        }
    }

    private fun sendSubscribeFrame(topic: String, subId: String) {
        val frame = StompFrame(
            command = StompFrame.SUBSCRIBE,
            headers = mapOf(
                "destination" to topic,
                "id" to subId,
                "ack" to "auto"
            )
        )
        registryScope?.launch {
            stompClient.send(frame)
        }
    }

    private fun sendUnsubscribeFrame(subId: String) {
        val frame = StompFrame(
            command = StompFrame.UNSUBSCRIBE,
            headers = mapOf("id" to subId)
        )
        registryScope?.launch {
            stompClient.send(frame)
        }
    }

    private fun resubscribeAll() {
        activeSubscriptions.forEach { (topic, subId) ->
            sendSubscribeFrame(topic, subId)
        }
    }
}
