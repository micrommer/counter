package com.micrommer.counter.service

import com.micrommer.counter.service.abstraction.Notifier
import org.springframework.stereotype.Service

@Service
class EmailNotifier : Notifier {
    override fun notify(recipients: List<String>, message: String) {

    }
}