package com.micrommer.counter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors

import springfox.documentation.builders.RequestHandlerSelectors

import springfox.documentation.spi.DocumentationType

import springfox.documentation.spring.web.plugins.Docket


@SpringBootApplication
class CounterApplication

fun main(args: Array<String>) {
    runApplication<CounterApplication>(*args)
}
