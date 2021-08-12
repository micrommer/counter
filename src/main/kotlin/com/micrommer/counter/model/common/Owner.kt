package com.micrommer.counter.model.common

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

/**
 * counter (com.micrommer.counter.model)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 2:00 PM, Tuesday
 */
data class Owner(
        @NotEmpty
        val fullName: String,
        @field:Email
        val email: String
)
