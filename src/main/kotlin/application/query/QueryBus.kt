package com.nekgambling.application.query

class QueryBus(private val handlers: List<IQueryHandler<*, *>>) {

    @Suppress("UNCHECKED_CAST")
    suspend fun <R> execute(query: IQuery<R>): R {
        val handler = handlers.find { it.queryType.isInstance(query) }
            ?: error("No handler registered for query ${query::class.simpleName}")

        return (handler as IQueryHandler<IQuery<R>, R>).handle(query)
    }
}
