package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.AutomationRule
import com.po4yka.ratatoskr.domain.repository.RuleRepository
import org.koin.core.annotation.Factory

@Factory
class GetRulesUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(): List<AutomationRule> = ruleRepository.listRules()
}
