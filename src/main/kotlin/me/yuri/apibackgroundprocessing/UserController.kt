package me.yuri.apibackgroundprocessing

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("users")
class UserController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/{userId}")
    private fun findUserById(
        @PathVariable userId: String
    ): Mono<ResponseEntity<String>> = userService
        .findUserById(userId)
        .map { ResponseEntity.ok(it) }
        .doOnSuccess { log.info("Success for $userId") }
        .doOnError { log.info("Error for $userId: ${it.message}")}
        .onErrorResume { Mono.just(ResponseEntity.internalServerError().body("The request processing was not successful")) }
}