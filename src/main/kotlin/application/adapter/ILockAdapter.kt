package com.nekgambling.application.adapter

interface ILockAdapter {

    suspend fun <T> withLock(key: String, block: suspend () -> T): T

}
