package com.nekgambling.domain.segment.repository

import com.nekgambling.domain.segment.model.SegmentResult

interface ISegmentRepository {

    suspend fun save(result: SegmentResult): SegmentResult

}