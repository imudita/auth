package com.example.auth.security

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class TokenBlacklist {
    private val blacklist = ConcurrentHashMap<String, Long>()

    fun add(token: String, expirationTime: Long) {
        blacklist[token] = expirationTime
    }

    fun isBlacklisted(token: String): Boolean {
        return blacklist.containsKey(token)
    }

    // Cleanup expired tokens periodically (optional)
    fun cleanup() {
        val now = System.currentTimeMillis()
        blacklist.entries.removeIf { it.value < now }
    }
}