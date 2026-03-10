package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import kotlin.reflect.KClass

class AmountExtractorProcess : ExtractorJourneyNodeProcess<IAmountExtractor>() {

    override val nodeType: KClass<IAmountExtractor> = IAmountExtractor::class

    override suspend fun extract(
        playerId: String,
        node: IAmountExtractor,
        payload: Payload,
    ): Payload {
        require(payload.containsKey(node.inputKey)) {
            "Parameters are not defined for ${node.inputKey}"
        }

        val param = payload.getValue(node.inputKey)
        require(param is Long) { "Parameter ${node.inputKey} is not a long" }

        return mapOf(IAmountExtractor.OUTPUT_KEY to node.calculate(param))
    }
}
