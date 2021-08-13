package com.micrommer.counter.service.abstraction

interface Notifier {
    /**
     * Notify: notifies recipients, information of recipient and the message structure depended on implementor
     */
    fun notify(recipients: List<String>, message: String)
}