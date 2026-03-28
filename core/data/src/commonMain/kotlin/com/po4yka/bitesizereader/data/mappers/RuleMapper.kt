package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.RuleDto
import com.po4yka.bitesizereader.data.remote.dto.RuleLogDto
import com.po4yka.bitesizereader.data.remote.dto.TestRuleResponseDto
import com.po4yka.bitesizereader.domain.model.AutomationRule
import com.po4yka.bitesizereader.domain.model.RuleLog
import com.po4yka.bitesizereader.domain.model.TestRuleResult

fun RuleDto.toDomain(): AutomationRule =
    AutomationRule(
        id = id,
        name = name,
        description = description,
        enabled = enabled,
        eventType = eventType,
        matchMode = matchMode,
        conditions = conditions,
        actions = actions,
        priority = priority,
        runCount = runCount,
        lastTriggeredAt = lastTriggeredAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun RuleLogDto.toDomain(): RuleLog =
    RuleLog(
        id = id,
        ruleId = ruleId,
        summaryId = summaryId,
        eventType = eventType,
        matched = matched,
        error = error,
        durationMs = durationMs,
        createdAt = createdAt,
    )

fun TestRuleResponseDto.toDomain(): TestRuleResult =
    TestRuleResult(
        matched = matched,
        conditionsResult = conditionsResult,
        wouldExecuteActions = wouldExecuteActions,
    )
