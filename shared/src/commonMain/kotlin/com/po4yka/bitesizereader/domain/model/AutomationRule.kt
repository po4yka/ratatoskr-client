package com.po4yka.bitesizereader.domain.model

import kotlinx.serialization.json.JsonObject

data class AutomationRule(
    val id: Int,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val eventType: String,
    val matchMode: String,
    val conditions: List<JsonObject>,
    val actions: List<JsonObject>,
    val priority: Int,
    val runCount: Int,
    val lastTriggeredAt: String?,
    val createdAt: String,
    val updatedAt: String,
)

data class RuleLog(
    val id: Int,
    val ruleId: Int,
    val summaryId: Int?,
    val eventType: String,
    val matched: Boolean,
    val error: String?,
    val durationMs: Int?,
    val createdAt: String,
)

data class TestRuleResult(
    val matched: Boolean,
    val conditionsResult: List<JsonObject>,
    val wouldExecuteActions: List<JsonObject>,
)
