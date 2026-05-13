package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.Rule as GeneratedRule
import com.po4yka.ratatoskr.api.generated.models.RuleExecutionLog as GeneratedRuleExecutionLog
import com.po4yka.ratatoskr.api.generated.models.RuleTestData as GeneratedRuleTestData
import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.model.RuleLog
import com.po4yka.ratatoskr.domain.model.TestRuleResult
import kotlinx.serialization.json.JsonObject

fun GeneratedRule.toDomain(): AutomationRule =
    AutomationRule(
        id = id.toInt(),
        name = name,
        description = description,
        enabled = enabled,
        eventType = eventType,
        matchMode = matchMode,
        conditions = conditions.filterIsInstance<JsonObject>(),
        actions = actions.filterIsInstance<JsonObject>(),
        priority = priority.toInt(),
        runCount = runCount.toInt(),
        lastTriggeredAt = lastTriggeredAt?.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
    )

fun GeneratedRuleExecutionLog.toDomain(): RuleLog =
    RuleLog(
        id = id.toInt(),
        ruleId = ruleId.toInt(),
        summaryId = summaryId?.toInt(),
        eventType = eventType,
        matched = matched,
        error = error,
        durationMs = durationMs?.toInt(),
        createdAt = createdAt.toString(),
    )

fun GeneratedRuleTestData.toDomain(): TestRuleResult =
    TestRuleResult(
        matched = matched,
        conditionsResult = conditionsResult.filterIsInstance<JsonObject>(),
        wouldExecuteActions = wouldExecuteActions.filterIsInstance<JsonObject>(),
    )
