package com.nekgambling.application.cqrs.command

import kotlin.reflect.KClass

interface ICommandHandler<C : ICommand> {
    val commandType: KClass<C>
    suspend fun handle(command: C)
}
