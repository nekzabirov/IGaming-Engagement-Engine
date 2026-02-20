package com.nekgambling.segment.repository

import com.nekgambling.segment.model.SegmentResult

interface ISegmentRepository {

    suspend fun save(result: SegmentResult): SegmentResult

}