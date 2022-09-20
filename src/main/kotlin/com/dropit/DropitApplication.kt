package com.dropit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DropitApplication

fun main(args: Array<String>) {
	runApplication<DropitApplication>(*args)
}
