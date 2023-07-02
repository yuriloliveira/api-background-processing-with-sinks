package me.yuri.apibackgroundprocessing

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import javax.annotation.PostConstruct

@Configuration
class SlowUserSinkConfig {
    @Bean
    @Qualifier(QUALIFIER_SLOW_USER_SINK)
    fun slowUserSink() = Sinks.many().unicast().onBackpressureBuffer<String>()
}

@Configuration
class BackgroundProcessingConfiguration(
    @Qualifier(QUALIFIER_SLOW_USER_SINK) private val slowUserSink: Sinks.Many<String>,
    private val userService: UserService,
    private val usersCache: UserCacheService,
    private val slowUserProperties: SlowUserProperties,
) {
    private val log = LoggerFactory.getLogger(BackgroundProcessingConfiguration::class.java)
    @PostConstruct
    fun subscribeToSink() {
        if (slowUserProperties.backgroundProcessingEnabled) {
            slowUserSink
                .asFlux()
                .doOnNext { log.info("Slow user emitted: {}", it)}
                .flatMap { userId -> userService.findUserByIdWithTimeout(userId, 20000).map { Pair(userId, it) } }
                .map { usersCache.put(it.first, it.second); it }
                .onErrorContinue{ err, _ -> log.error("Error on emitted slow user", err) }
                .publishOn(Schedulers.newSingle("bg-processing"))
                .subscribe { log.info("Slow user loaded in background: {}", it) }
        }
    }
}

const val QUALIFIER_SLOW_USER_SINK = "slowUserSink"