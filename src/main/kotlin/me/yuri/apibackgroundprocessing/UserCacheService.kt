package me.yuri.apibackgroundprocessing

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class UserCacheService {
    private val usersMap: MutableMap<String, String> = mutableMapOf()

    fun put(userId: String, user: String) {
        usersMap[userId] = user
    }

    fun get(userId: String): Mono<String> =
        Mono
            .justOrEmpty(usersMap.getOrDefault(userId, null))
            .delayElement(Duration.ofMillis(20))
}