package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.api.RulesApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1RulesRequest
import com.po4yka.ratatoskr.api.generated.models.V1RulesRuleIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1RulesRuleIdTestRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.RuleDto
import com.po4yka.ratatoskr.data.remote.dto.RuleListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RuleLogListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TestRuleResponseDto
import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.model.RuleLog
import com.po4yka.ratatoskr.domain.model.TestRuleResult
import com.po4yka.ratatoskr.domain.repository.RuleRepository
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

@Single(binds = [RuleRepository::class])
class RuleRepositoryImpl : RuleRepository {
    override suspend fun listRules(): List<AutomationRule> {
        val envelope =
            RulesApi.listRulesV1RulesGet().unwrap().decodeEnvelope<RuleListResponseDto>()
        return envelope?.rules?.map { it.toDomain() } ?: emptyList()
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
        val rule =
            RulesApi.createRuleV1RulesPost(
                body =
                    V1RulesRequest(
                        name = name,
                        eventType = eventType,
                        actions = actions,
                        conditions = conditions,
                        matchMode = matchMode,
                        priority = priority.toLong(),
                        description = description,
                    ),
            ).unwrap().decodeEnvelope<RuleDto>()
        return requireNotNull(rule) { "Server returned no data for rule creation" }.toDomain()
    }

    override suspend fun getRule(ruleId: Int): AutomationRule {
        val rule =
            RulesApi.getRuleV1RulesRuleIdGet(ruleId = ruleId.toLong())
                .unwrap()
                .decodeEnvelope<RuleDto>()
        return requireNotNull(rule) { "Rule $ruleId not found" }.toDomain()
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
        val rule =
            RulesApi.updateRuleV1RulesRuleIdPatch(
                ruleId = ruleId.toLong(),
                body =
                    V1RulesRuleIdRequest(
                        name = name,
                        eventType = eventType,
                        conditions = conditions,
                        actions = actions,
                        matchMode = matchMode,
                        priority = priority?.toLong(),
                        description = description,
                        enabled = enabled,
                    ),
            ).unwrap().decodeEnvelope<RuleDto>()
        return requireNotNull(rule) { "Server returned no data for rule update" }.toDomain()
    }

    override suspend fun deleteRule(ruleId: Int) {
        RulesApi.deleteRuleV1RulesRuleIdDelete(ruleId = ruleId.toLong()).unwrap()
    }

    override suspend fun testRule(
        ruleId: Int,
        summaryId: Int,
    ): TestRuleResult {
        val result =
            RulesApi.dryRunRuleV1RulesRuleIdTestPost(
                ruleId = ruleId.toLong(),
                body = V1RulesRuleIdTestRequest(summaryId = summaryId.toLong()),
            ).unwrap().decodeEnvelope<TestRuleResponseDto>()
        return requireNotNull(result) { "Server returned no data for rule test" }.toDomain()
    }

    override suspend fun getRuleLogs(
        ruleId: Int,
        limit: Int,
        offset: Int,
    ): List<RuleLog> {
        val envelope =
            RulesApi.listExecutionLogsV1RulesRuleIdLogsGet(
                ruleId = ruleId.toLong(),
                limit = limit.toLong(),
                offset = offset.toLong(),
            ).unwrap().decodeEnvelope<RuleLogListResponseDto>()
        return envelope?.logs?.map { it.toDomain() } ?: emptyList()
    }
}

private inline fun <reified T> JsonElement.decodeEnvelope(): T? {
    val obj = (this as? JsonObject) ?: return null
    val data = obj["data"] ?: return null
    if (data is kotlinx.serialization.json.JsonNull) return null
    return Api.json.decodeFromJsonElement(
        kotlinx.serialization.serializer<T>(),
        data,
    )
}
