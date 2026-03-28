package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CreateRuleRequestDto
import com.po4yka.bitesizereader.data.remote.dto.RuleDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RuleDto
import com.po4yka.bitesizereader.data.remote.dto.RuleListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RuleLogListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TestRuleRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TestRuleResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateRuleRequestDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [RulesApi::class])
class KtorRulesApi(private val client: HttpClient) : RulesApi {
    override suspend fun listRules(): ApiResponseDto<RuleListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/rules").body()
        }

    override suspend fun createRule(request: CreateRuleRequestDto): ApiResponseDto<RuleDto> {
        return client.post("v1/rules") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRule(ruleId: Int): ApiResponseDto<RuleDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/rules/$ruleId").body()
        }

    override suspend fun updateRule(ruleId: Int, request: UpdateRuleRequestDto): ApiResponseDto<RuleDto> {
        return client.patch("v1/rules/$ruleId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteRule(ruleId: Int): ApiResponseDto<RuleDeleteResponseDto> {
        return client.delete("v1/rules/$ruleId").body()
    }

    override suspend fun testRule(ruleId: Int, request: TestRuleRequestDto): ApiResponseDto<TestRuleResponseDto> {
        return client.post("v1/rules/$ruleId/test") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getRuleLogs(ruleId: Int, limit: Int, offset: Int): ApiResponseDto<RuleLogListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/rules/$ruleId/logs") {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body()
        }
}
