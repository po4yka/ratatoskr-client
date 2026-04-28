package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.model.RuleLog
import com.po4yka.ratatoskr.domain.model.TestRuleResult
import kotlinx.serialization.json.JsonObject

interface RuleRepository {
    suspend fun listRules(): List<AutomationRule>

    suspend fun createRule(
        name: String,
        eventType: String,
        actions: List<JsonObject>,
        conditions: List<JsonObject> = emptyList(),
        matchMode: String = "all",
        priority: Int = 0,
        description: String? = null,
    ): AutomationRule

    suspend fun getRule(ruleId: Int): AutomationRule

    suspend fun updateRule(
        ruleId: Int,
        name: String? = null,
        eventType: String? = null,
        conditions: List<JsonObject>? = null,
        actions: List<JsonObject>? = null,
        matchMode: String? = null,
        priority: Int? = null,
        description: String? = null,
        enabled: Boolean? = null,
    ): AutomationRule

    suspend fun deleteRule(ruleId: Int)

    suspend fun testRule(
        ruleId: Int,
        summaryId: Int,
    ): TestRuleResult

    suspend fun getRuleLogs(
        ruleId: Int,
        limit: Int = 50,
        offset: Int = 0,
    ): List<RuleLog>
}
