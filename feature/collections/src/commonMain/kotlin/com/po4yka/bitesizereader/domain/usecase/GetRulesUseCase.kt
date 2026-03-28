package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.AutomationRule
import com.po4yka.bitesizereader.domain.repository.RuleRepository
import org.koin.core.annotation.Factory

@Factory
class GetRulesUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke(): List<AutomationRule> = ruleRepository.listRules()
}
