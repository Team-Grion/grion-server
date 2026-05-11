package com.project.grionserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class GrionServerApplication

fun main(args: Array<String>) {
    runApplication<GrionServerApplication>(*args)
}
