package com.example.creatingabatchservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreatingaBatchServiceApplication

fun main(args: Array<String>) {
    System.exit(SpringApplication.exit(runApplication<CreatingaBatchServiceApplication>(*args)))
}
