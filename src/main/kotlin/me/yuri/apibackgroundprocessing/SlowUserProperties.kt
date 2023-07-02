package me.yuri.apibackgroundprocessing

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app.config.slow-user")
data class SlowUserProperties(
    var backgroundProcessingEnabled: Boolean = false
)
