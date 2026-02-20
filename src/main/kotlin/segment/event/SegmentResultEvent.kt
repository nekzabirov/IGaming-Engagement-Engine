package com.nekgambling.segment.event

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.segment.model.SegmentResult

data class SegmentResultEvent(val result: SegmentResult) : IEventAdapter.AppEvent