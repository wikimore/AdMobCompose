package io.github.wikimore.admobcomposeexample.states

data class AdState(
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)
