package com.nekgambling.api

import com.nekgambling.api.dto.*
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.infrastructure.journey.NomenclatureRegistry
import com.nekgambling.infrastructure.journey.condition.IConditionJourneyNode
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*
import org.koin.ktor.ext.inject

fun Routing.journeyApi() {
    val repository by inject<IJourneyRepository>()
    val nomenclatureRegistry by inject<NomenclatureRegistry>()

    route("journey") {
        post {
            val request = call.receive<CreateJourneyRequest>()
            val head = resolveNode(request.head, nomenclatureRegistry)
            val journey = Journey(identity = request.identity, head = head)
            val saved = repository.save(journey)
            call.respond(HttpStatusCode.Created, toResponse(saved, nomenclatureRegistry))
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")
            repository.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun resolveNode(
    req: JourneyNodeRequest,
    registry: NomenclatureRegistry,
): IJourneyNode {
    val nomenclature = registry.get(req.type) as com.nekgambling.domain.strategy.JourneyNodeNomenclature<IJourneyNode>

    val assetsMap = jsonElementMapToAnyMap(req.assets)
    val baseNode = nomenclature.fromAssetsMap(assetsMap)

    val next = req.next?.let { resolveNode(it, registry) }
    val matchNode = req.matchNode?.let { resolveNode(it, registry) }
    val notMatchNode = req.notMatchNode?.let { resolveNode(it, registry) }

    return nomenclature.withLinks(baseNode, next = next, matchNode = matchNode, notMatchNode = notMatchNode)
}

@Suppress("UNCHECKED_CAST")
private fun toResponse(journey: Journey, registry: NomenclatureRegistry): JourneyResponse {
    return JourneyResponse(
        id = journey.id,
        identity = journey.identity,
        head = toNodeResponse(journey.head, registry),
    )
}

@Suppress("UNCHECKED_CAST")
private fun toNodeResponse(node: IJourneyNode, registry: NomenclatureRegistry): JourneyNodeResponse {
    val nomenclature = registry.findByNode(node) as com.nekgambling.domain.strategy.JourneyNodeNomenclature<IJourneyNode>
    val assetsJson = anyMapToJsonElementMap(nomenclature.toAssetsMap(node))

    val isCondition = node is IConditionJourneyNode
    return JourneyNodeResponse(
        id = node.id,
        type = nomenclature.identity,
        assets = assetsJson,
        next = if (!isCondition) node.next?.let { toNodeResponse(it, registry) } else null,
        matchNode = if (isCondition) (node as IConditionJourneyNode).matchNode?.let { toNodeResponse(it, registry) } else null,
        notMatchNode = if (isCondition) (node as IConditionJourneyNode).notMatchNode?.let { toNodeResponse(it, registry) } else null,
    )
}

private fun jsonElementMapToAnyMap(map: Map<String, JsonElement>): Map<String, Any> =
    map.mapValues { (_, element) -> jsonElementToAny(element) }

private fun jsonElementToAny(element: JsonElement): Any = when (element) {
    is JsonPrimitive -> when {
        element.isString -> element.content
        element.booleanOrNull != null -> element.boolean
        element.longOrNull != null -> element.long
        element.doubleOrNull != null -> element.double
        else -> element.content
    }
    is JsonObject -> element.mapValues { (_, v) -> jsonElementToAny(v) }
    is JsonArray -> element.map { jsonElementToAny(it) }
}

@Suppress("UNCHECKED_CAST")
private fun anyMapToJsonElementMap(map: Map<String, Any>): Map<String, JsonElement> =
    map.mapValues { (_, value) -> anyToJsonElement(value) }

private fun anyToJsonElement(value: Any): JsonElement = when (value) {
    is String -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is Map<*, *> -> JsonObject((value as Map<String, Any>).mapValues { (_, v) -> anyToJsonElement(v) })
    is List<*> -> JsonArray(value.map { anyToJsonElement(it!!) })
    else -> JsonPrimitive(value.toString())
}
