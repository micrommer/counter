package com.micrommer.counter.service

import com.micrommer.counter.service.abstraction.Notifier
import org.springframework.stereotype.Service

@Service
class EmailNotifier : Notifier {
    private val color = "\u001B[34m"
    private val reset = "\u001B[0m"
    override fun notify(recipients: List<String>, message: String) {
        println("$color------------------------------------------------------------------------------")
        println("recipients : $recipients")
        println(message)
        println("------------------------------------------------------------------------------$reset")
    }
}