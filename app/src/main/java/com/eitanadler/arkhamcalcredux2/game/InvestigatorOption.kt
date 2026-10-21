package com.eitanadler.arkhamcalcredux2.game

import androidx.annotation.StringRes
import com.eitanadler.arkhamcalcredux2.R

internal enum class InvestigatorOption(
    val helpLabel: String,
    val textLabel: String,
    @StringRes val singleUseChanceNotice: Int?,
) {
    NORMAL(
        helpLabel = "Normal",
        textLabel = "Other",
        singleUseChanceNotice = null,
    ),
    MANDY(
        helpLabel = "Mandy",
        textLabel = "Mandy Thompson",
        singleUseChanceNotice = R.string.mandy_chances_toast,
    ),
    SKIDS(
        helpLabel = "Skids",
        textLabel = "Skids O' Tool",
        singleUseChanceNotice = R.string.skids_chances_toast,
    ),
}
