package com.micrommer.counter.model.dto

/**
 * counter (com.micrommer.counter.model.dto)
 * @author : imanbhlool
 * @since : Aug/10/2021 - 5:00 PM, Tuesday
 */
interface DaoCompatible<T> {
    /**
     * retrieves corresponding DAO
     */
    fun getDao(): T
}