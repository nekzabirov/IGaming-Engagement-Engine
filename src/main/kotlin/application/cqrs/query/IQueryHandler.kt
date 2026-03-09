package com.nekgambling.application.cqrs.query

import kotlin.reflect.KClass

interface IQueryHandler<Q : IQuery<R>, R> {
    val queryType: KClass<Q>
    suspend fun handle(query: Q): R
}
