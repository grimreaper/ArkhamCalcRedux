package com.eitanadler.arkhamcalcredux2.screenshots

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eitanadler.arkhamcalcredux2.ArkhamCalcHelpTab
import com.eitanadler.arkhamcalcredux2.MainArkhamTab
import com.eitanadler.arkhamcalcredux2.ux.ArkhamCalcScaffold
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class ScreenshotGenerationTest {
    //    @get:Rule(order = 0)
//    val activityRule = ActivityScenarioRule(ArkhamCalc::class.java)

    @Rule(order = 1)
    @JvmField
    val localeTestRule = LocaleTestRule()

    @get:Rule(order = 2)
    val composeTestRule: ComposeContentTestRule =
        createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        CleanStatusBar.enableWithDefaults()
    }

    @After
    fun cleanUp() {
        CleanStatusBar.disable()
    }

    @Test
    fun takeScreenshotOfMainScreen() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        composeTestRule.setContent {
            // not sure why ArkhamCalcScaffold isn't helping here
            MainArkhamTab(
                goToHelpScreen = {
                },
            )
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("calc_main_screen_screen_baseline")
    }

    @Test
    fun takeScreenshotOfHelpScreen() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())

        composeTestRule.setContent {
            ArkhamCalcScaffold {
                ArkhamCalcHelpTab()
            }
        }
        composeTestRule.waitForIdle()
        Screengrab.screenshot("calc_help_screen_screen_baseline")
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun afterAll() {
            CleanStatusBar.disable()
        }

        @JvmStatic
        @BeforeClass
        fun beforeAll() {
            CleanStatusBar.enableWithDefaults()
        }
    }
}
