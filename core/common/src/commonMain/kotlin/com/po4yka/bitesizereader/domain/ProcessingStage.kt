package com.po4yka.bitesizereader.domain.model

enum class ProcessingStage {
    UNSPECIFIED,
    QUEUED,
    EXTRACTION,
    SUMMARIZATION,
    SAVING,
    DONE,
}
