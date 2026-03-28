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

interface RulesApi {
    suspend fun listRules(): ApiResponseDto<RuleListResponseDto>

    suspend fun createRule(request: CreateRuleRequestDto): ApiResponseDto<RuleDto>

    suspend fun getRule(ruleId: Int): ApiResponseDto<RuleDto>

    suspend fun updateRule(
        ruleId: Int,
        request: UpdateRuleRequestDto,
    ): ApiResponseDto<RuleDto>

    suspend fun deleteRule(ruleId: Int): ApiResponseDto<RuleDeleteResponseDto>

    suspend fun testRule(
        ruleId: Int,
        request: TestRuleRequestDto,
    ): ApiResponseDto<TestRuleResponseDto>

    suspend fun getRuleLogs(
        ruleId: Int,
        limit: Int = 50,
        offset: Int = 0,
    ): ApiResponseDto<RuleLogListResponseDto>
}
