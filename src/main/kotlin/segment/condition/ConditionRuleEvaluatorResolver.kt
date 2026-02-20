package com.nekgambling.segment.condition

class ConditionRuleEvaluatorResolver(
    private val evaluators: List<IConditionRuleEvaluator<*>>
) {

    suspend fun evaluate(playerId: String, rule: IConditionRule): Boolean {
        val evaluator = evaluators.find { it.condition.isInstance(rule) }
            ?: error("Cannot find evaluator for condition rule ${rule::class.simpleName}")

        return evaluateUnchecked(evaluator, playerId, rule)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : IConditionRule> evaluateUnchecked(
        evaluator: IConditionRuleEvaluator<T>,
        playerId: String,
        rule: IConditionRule
    ): Boolean = evaluator.evaluate(playerId, rule as T)

}
