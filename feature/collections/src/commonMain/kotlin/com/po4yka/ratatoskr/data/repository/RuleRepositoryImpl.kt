package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.RulesApi
import com.po4yka.ratatoskr.data.remote.dto.CreateRuleRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TestRuleRequestDto
import com.po4yka.ratatoskr.data.remote.dto.UpdateRuleRequestDto
import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.model.RuleLog
import com.po4yka.ratatoskr.domain.model.TestRuleResult
import com.po4yka.ratatoskr.domain.repository.RuleRepository
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

@Single(binds = [RuleRepository::class])
class RuleRepositoryImpl(
    private val rulesApi: RulesApi,
) : RuleRepository {
    override suspend fun listRules(): List<AutomationRule> {
        val response = rulesApi.listRules()
        return response.data?.rules?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun createRule(
        name: String,
        eventType: String,
        actions: List<JsonObject>,
        conditions: List<JsonObject>,
        matchMode: String,
        priority: Int,
        description: String?,
    ): AutomationRule {
        val response =
            rulesApi.createRule(
                CreateRuleRequestDto(
                    name = name,
                    eventType = eventType,
                    actions = actions,
                    conditions = conditions,
                    matchMode = matchMode,
                    priority = priority,
                    description = description,
                ),
            )
        return requireNotNull(response.data) { "Server returned no data for rule creation" }.toDomain()
    }

    override suspend fun getRule(ruleId: Int): AutomationRule {
        val response = rulesApi.getRule(ruleId)
        return requireNotNull(response.data) { "Rule $ruleId not found" }.toDomain()
    }

    override suspend fun updateRule(
        ruleId: Int,
        name: String?,
        eventType: String?,
        conditions: List<JsonObject>?,
        actions: List<JsonObject>?,
        matchMode: String?,
        priority: Int?,
        description: String?,
        enabled: Boolean?,
    ): AutomationRule {
        val response =
            rulesApi.updateRule(
                ruleId,
                UpdateRuleRequestDto(
                    name = name,
                    eventType = eventType,
                    conditions = conditions,
                    actions = actions,
                    matchMode = matchMode,
                    priority = priority,
                    description = description,
                    enabled = enabled,
                ),
            )
        return requireNotNull(response.data) { "Server returned no data for rule update" }.toDomain()
    }

    override suspend fun deleteRule(ruleId: Int) {
        rulesApi.deleteRule(ruleId)
    }

    override suspend fun testRule(
        ruleId: Int,
        summaryId: Int,
    ): TestRuleResult {
        val response = rulesApi.testRule(ruleId, TestRuleRequestDto(summaryId = summaryId))
        return requireNotNull(response.data) { "Server returned no data for rule test" }.toDomain()
    }

    override suspend fun getRuleLogs(
        ruleId: Int,
        limit: Int,
        offset: Int,
    ): List<RuleLog> {
        val response = rulesApi.getRuleLogs(ruleId, limit = limit, offset = offset)
        return response.data?.logs?.map { it.toDomain() } ?: emptyList()
    }
}
