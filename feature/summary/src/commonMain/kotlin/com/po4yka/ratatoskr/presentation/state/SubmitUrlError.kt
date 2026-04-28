package com.po4yka.ratatoskr.presentation.state

sealed class SubmitUrlError {
    data object InvalidUrl : SubmitUrlError()

    data object DuplicateUrl : SubmitUrlError()

    data object NetworkError : SubmitUrlError()

    data object ServerError : SubmitUrlError()

    data class Unknown(val message: String) : SubmitUrlError()
}
