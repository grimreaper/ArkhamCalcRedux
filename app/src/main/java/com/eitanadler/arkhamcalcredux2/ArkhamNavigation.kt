package com.eitanadler.arkhamcalcredux2

internal sealed interface ArkhamNavigation

internal data object CalculatorUXScreen : ArkhamNavigation

internal data class HelpScreen(
    val topic: String,
) : ArkhamNavigation

internal data object LibrariesScreen : ArkhamNavigation
