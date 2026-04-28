package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.repository.RuleRepository
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Factory

@Factory
class CreateRuleUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(
        name: String,
        eventType: String,
        actions: List<JsonObject>,
        conditions: List<JsonObject> = emptyList(),
        matchMode: String = "all",
        priority: Int = 0,
        description: String? = null,
    ): AutomationRule =
        ruleRepository.createRule(
            name = name,
            eventType = eventType,
            actions = actions,
            conditions = conditions,
            matchMode = matchMode,
            priority = priority,
            description = description,
        )
}
