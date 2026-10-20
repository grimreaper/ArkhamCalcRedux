package com.eitanadler.arkhamcalcredux2

internal sealed interface ArkhamNavigation
internal data object CalculatorUX: ArkhamNavigation
internal data class HelpScreen(val topic: String): ArkhamNavigation