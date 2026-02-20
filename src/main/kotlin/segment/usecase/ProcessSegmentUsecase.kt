package com.nekgambling.segment.usecase

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.adapter.ILockAdapter
import com.nekgambling.segment.event.SegmentResultEvent
import com.nekgambling.segment.model.Segment
import com.nekgambling.segment.model.SegmentResult
import com.nekgambling.segment.repository.IConditionRepository
import com.nekgambling.segment.repository.ISegmentRepository
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