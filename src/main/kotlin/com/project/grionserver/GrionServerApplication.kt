package com.project.grionserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync

@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
class GrionServerApplication

fun main(args: Array<String>) {
    runApplication<GrionServerApplication>(*args)
}
