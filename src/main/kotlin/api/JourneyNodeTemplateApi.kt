package com.nekgambling.api

import com.nekgambling.api.dto.JourneyNodeTemplateResponse
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Routing.journeyNodeTemplateApi() = route("journey") {
    route("node") {
        get("templates") {
            val nomenclatures = application.getKoin().getAll<JourneyNodeNomenclature<*>>()
            val templates = nomenclatures.map { n ->
                JourneyNodeTemplateResponse(
                    identity = n.identity,
                    category = n.category,
                    inputParams = n.inputParams(),
                    outputParams = n.outputParams(),
                    assets = n.assetsSchema(),
                )
            }
            call.respond(templates)
        }
    }
}
