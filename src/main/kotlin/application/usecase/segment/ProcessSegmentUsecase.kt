package com.nekgambling.application.usecase.segment

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.application.event.segment.SegmentResultEvent
import com.nekgambling.domain.segment.model.Segment
import com.nekgambling.domain.segment.model.SegmentResult
import com.nekgambling.domain.condition.repository.IConditionRepository
import com.nekgambling.domain.segment.repository.ISegmentRepository
import kotlin.jvm.optionals.getOrElse

class ProcessSegmentUsecase(
    private val segmentRepository: ISegmentRepository,
    private val conditionRepository: IConditionRepository,
    private val eventAdapter: IEventAdapter,
    private val lockAdapter: ILockAdapter
) {

    suspend operator fun invoke(playerId: String, segment: Segment): Result<SegmentResult> = runCatching {
        lockAdapter.withLock("segment:${segment.id}:player:$playerId") {
            process(playerId, segment)
        }
    }

    private suspend fun process(playerId: String, segment: Segment): SegmentResult {
        val conditions = segment.conditions

        val isAllPassed = conditions.all { condition ->
            conditionRepository
                .findResultBy(playerId = playerId, conditionId = condition.id)
                .map { it.passed }
                .getOrElse { false }
        }

        return SegmentResult(playerId = playerId, segment = segment, passed = isAllPassed)
            .let { segmentRepository.save(it) }
            .also { eventAdapter.publish(SegmentResultEvent(it)) }
    }

}