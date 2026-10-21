package com.eitanadler.arkhamcalcredux2

internal sealed interface ArkhamNavigationKey

internal data object CalculatorUXScreen : ArkhamNavigationKey

internal data class HelpScreen(
    val topic: String,
) : ArkhamNavigationKey

internal data object LibrariesScreen : ArkhamNavigationKey
