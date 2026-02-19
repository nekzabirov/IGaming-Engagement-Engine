package com.nekgambling.core.adapter

interface IEventAdapter {

    interface AppEvent

    suspend fun publish(event: AppEvent)

}