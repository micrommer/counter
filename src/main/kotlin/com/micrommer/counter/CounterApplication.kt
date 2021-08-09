package com.micrommer.counter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CounterApplication

fun main(args: Array<String>) {
	runApplication<CounterApplication>(*args)
}
