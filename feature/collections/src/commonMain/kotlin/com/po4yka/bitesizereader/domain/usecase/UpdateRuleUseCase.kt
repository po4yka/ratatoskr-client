package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.AutomationRule
import com.po4yka.bitesizereader.domain.repository.RuleRepository
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Factory

@Factory
class UpdateRuleUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(
        ruleId: Int,
        name: String? = null,
        eventType: String? = null,
        conditions: List<JsonObject>? = null,
        actions: List<JsonObject>? = null,
        matchMode: String? = null,
        priority: Int? = null,
        description: String? = null,
        enabled: Boolean? = null,
    ): AutomationRule = ruleRepository.updateRule(
        ruleId = ruleId,
        name = name,
        eventType = eventType,
        conditions = conditions,
        actions = actions,
        matchMode = matchMode,
        priority = priority,
        description = description,
        enabled = enabled,
    )
}
