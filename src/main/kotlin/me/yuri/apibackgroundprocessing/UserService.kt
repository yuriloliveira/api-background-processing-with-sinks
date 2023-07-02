package me.yuri.apibackgroundprocessing

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeoutException

@Service
class UserService(
    private val slowUserService: SlowUserService,
    private val usersCache: UserCacheService
) {
    private val webClient = WebClient.builder().baseUrl("http://localhost:8181/users").build()
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun findUserById(userId: String): Mono<String> =
        findUserByIdWithTimeout(userId, 4000)
            .doOnError {
                if (it is TimeoutException || it.cause is TimeoutException) {
                    slowUserService.handleSlowUser(userId)
                }
            }

    fun findUserByIdWithTimeout(userId: String, timeoutInMillis: Long): Mono<String> =
        usersCache
            .get(userId)
            .doOnNext { log.info("{} was cached. Not requesting from Users API.", userId) }
            .switchIfEmpty(
                webClient
                    .get()
                    .uri("/$userId")
                    .retrieve()
                    .bodyToMono<String>().map { "Hello, $it!" }
                    .doOnNext { usersCache.put(userId, it) }
                    .timeout(Duration.ofMillis(timeoutInMillis))
            )

}
