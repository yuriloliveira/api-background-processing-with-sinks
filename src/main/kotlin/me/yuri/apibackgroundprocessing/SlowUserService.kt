package me.yuri.apibackgroundprocessing

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class SlowUserService(
    @Qualifier(QUALIFIER_SLOW_USER_SINK) private val slowUsersSink: Sinks.Many<String>,
    private val slowUserProperties: SlowUserProperties
) {

    fun handleSlowUser(userId: String) {
        if (slowUserProperties.backgroundProcessingEnabled) {
            slowUsersSink.tryEmitNext(userId)
        }
    }
}
