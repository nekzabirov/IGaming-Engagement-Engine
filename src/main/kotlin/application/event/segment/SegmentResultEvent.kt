package com.nekgambling.application.event.segment

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.segment.model.SegmentResult

data class SegmentResultEvent(val result: SegmentResult) : IEventAdapter.AppEvent