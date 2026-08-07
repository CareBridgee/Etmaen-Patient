package com.carenest.data.socket.stomp

object StompFrameParser {

    private const val NULL_CHARACTER = "\u0000"
    private const val NEWLINE = "\n"

    /**
     * Parses a raw STOMP text frame into a [StompFrame] object.
     */
    fun parse(rawText: String): StompFrame? {
        if (rawText == NEWLINE) {
            // Heartbeat
            return StompFrame(command = "HEARTBEAT")
        }

        // Remove the trailing NULL character that terminates STOMP frames
        val text = if (rawText.endsWith(NULL_CHARACTER)) {
            rawText.dropLast(1)
        } else {
            rawText
        }

        val lines = text.split(NEWLINE)
        if (lines.isEmpty() || lines[0].isBlank()) {
            return null
        }

        val command = lines[0]
        val headers = mutableMapOf<String, String>()
        var bodyStartIndex = -1

        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.isEmpty()) {
                // An empty line marks the end of headers and beginning of body
                bodyStartIndex = i + 1
                break
            }

            val separatorIndex = line.indexOf(":")
            if (separatorIndex > 0) {
                val key = line.substring(0, separatorIndex).trim()
                val value = line.substring(separatorIndex + 1).trim()
                headers[key] = value
            }
        }

        val body = if (bodyStartIndex != -1 && bodyStartIndex < lines.size) {
            lines.subList(bodyStartIndex, lines.size).joinToString(NEWLINE)
        } else {
            null
        }

        return StompFrame(command = command, headers = headers, body = body)
    }

    /**
     * Serializes a [StompFrame] to a raw text string for sending.
     */
    fun serialize(frame: StompFrame): String {
        val builder = StringBuilder()
        builder.append(frame.command).append(NEWLINE)
        
        for ((key, value) in frame.headers) {
            builder.append(key).append(":").append(value).append(NEWLINE)
        }
        
        builder.append(NEWLINE) // Empty line separates headers from body
        
        if (frame.body != null) {
            builder.append(frame.body)
        }
        
        builder.append(NULL_CHARACTER)
        return builder.toString()
    }
}
