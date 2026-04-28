package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.RuleRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteRuleUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(ruleId: Int) = ruleRepository.deleteRule(ruleId)
}
