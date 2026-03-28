package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class RuleDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("enabled") val enabled: Boolean = true,
    @SerialName("eventType") val eventType: String,
    @SerialName("matchMode") val matchMode: String = "all",
    @SerialName("conditions") val conditions: List<JsonObject> = emptyList(),
    @SerialName("actions") val actions: List<JsonObject> = emptyList(),
    @SerialName("priority") val priority: Int = 0,
    @SerialName("runCount") val runCount: Int = 0,
    @SerialName("lastTriggeredAt") val lastTriggeredAt: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
)

@Serializable
data class RuleListResponseDto(
    @SerialName("rules") val rules: List<RuleDto>,
)

@Serializable
data class CreateRuleRequestDto(
    @SerialName("name") val name: String,
    @SerialName("event_type") val eventType: String,
    @SerialName("conditions") val conditions: List<JsonObject> = emptyList(),
    @SerialName("actions") val actions: List<JsonObject>,
    @SerialName("match_mode") val matchMode: String = "all",
    @SerialName("priority") val priority: Int = 0,
    @SerialName("description") val description: String? = null,
)

@Serializable
data class UpdateRuleRequestDto(
    @SerialName("name") val name: String? = null,
    @SerialName("event_type") val eventType: String? = null,
    @SerialName("conditions") val conditions: List<JsonObject>? = null,
    @SerialName("actions") val actions: List<JsonObject>? = null,
    @SerialName("match_mode") val matchMode: String? = null,
    @SerialName("priority") val priority: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("enabled") val enabled: Boolean? = null,
)

@Serializable
data class TestRuleRequestDto(
    @SerialName("summary_id") val summaryId: Int,
)

@Serializable
data class TestRuleResponseDto(
    @SerialName("matched") val matched: Boolean,
    @SerialName("conditions_result") val conditionsResult: List<JsonObject> = emptyList(),
    @SerialName("would_execute_actions") val wouldExecuteActions: List<JsonObject> = emptyList(),
)

@Serializable
data class RuleLogDto(
    @SerialName("id") val id: Int,
    @SerialName("ruleId") val ruleId: Int,
    @SerialName("summaryId") val summaryId: Int? = null,
    @SerialName("eventType") val eventType: String,
    @SerialName("matched") val matched: Boolean,
    @SerialName("conditionsResult") val conditionsResult: List<JsonObject>? = null,
    @SerialName("actionsTaken") val actionsTaken: List<JsonObject>? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("durationMs") val durationMs: Int? = null,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
data class RuleLogListResponseDto(
    @SerialName("logs") val logs: List<RuleLogDto>,
)

@Serializable
data class RuleDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: Int,
)
