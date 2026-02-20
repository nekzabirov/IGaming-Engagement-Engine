package com.nekgambling.application.adapter

interface IEventAdapter {

    interface AppEvent

    suspend fun publish(event: AppEvent)

}