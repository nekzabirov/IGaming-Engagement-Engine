package com.nekgambling.api.command

import kotlin.reflect.KClass

interface ICommandHandler<C : ICommand<R>, R> {
    val commandType: KClass<C>
    suspend fun handle(command: C): R
}
