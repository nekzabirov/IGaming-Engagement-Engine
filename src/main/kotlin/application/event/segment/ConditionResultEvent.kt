package com.nekgambling.application.event.segment

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.condition.model.ConditionResult

data class ConditionResultEvent(val result: ConditionResult) : IEventAdapter.AppEvent