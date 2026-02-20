package com.nekgambling.segment.event

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.segment.model.ConditionResult

data class ConditionResultEvent(val result: ConditionResult) : IEventAdapter.AppEvent