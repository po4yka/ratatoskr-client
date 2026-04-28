package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.TestRuleResult
import com.po4yka.ratatoskr.domain.repository.RuleRepository
import org.koin.core.annotation.Factory

@Factory
class TestRuleUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(
        ruleId: Int,
        summaryId: Int,
    ): TestRuleResult = ruleRepository.testRule(ruleId = ruleId, summaryId = summaryId)
}
