package com.nekgambling.api.command

class CommandBus(private val handlers: List<ICommandHandler<*, *>>) {

    @Suppress("UNCHECKED_CAST")
    suspend fun <R> execute(command: ICommand<R>): R {
        val handler = handlers.find { it.commandType.isInstance(command) }
            ?: error("No handler registered for command ${command::class.simpleName}")

        return (handler as ICommandHandler<ICommand<R>, R>).handle(command)
    }
}
