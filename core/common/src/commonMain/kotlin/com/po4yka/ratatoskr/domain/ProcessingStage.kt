package com.po4yka.ratatoskr.domain.model

enum class ProcessingStage {
    UNSPECIFIED,
    QUEUED,
    EXTRACTION,
    SUMMARIZATION,
    SAVING,
    DONE,
}
