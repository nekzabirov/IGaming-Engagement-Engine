package com.nekgambling.core.adapter

interface ILockAdapter {

    suspend fun <T> withLock(key: String, block: suspend () -> T): T

}
