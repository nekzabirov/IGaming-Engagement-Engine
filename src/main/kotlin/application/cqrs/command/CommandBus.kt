package com.nekgambling.application.cqrs.command

class CommandBus(private val handlers: List<ICommandHandler<*>>) {

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(command: ICommand) {
        handlers
            .filter { it.commandType.isInstance(command) }
            .forEach { (it as ICommandHandler<ICommand>).handle(command) }
    }
}
