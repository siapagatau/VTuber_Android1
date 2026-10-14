package com.dtxpads.vtuber.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dtxpads.vtuber.ui.theme.BgColor
import com.dtxpads.vtuber.ui.theme.TextMain

private const val SCREEN_HOME = "home"
private const val SCREEN_SETTINGS = "settings"

/** Navigasi minimal CP1: Home <-> Pengaturan. [resumeTick] naik tiap Activity onResume (refresh status izin). */
@Composable
fun VTuberApp(resumeTick: Int) {
    var screen by rememberSaveable { mutableStateOf(SCREEN_HOME) }
    Surface(modifier = Modifier.fillMaxSize(), color = BgColor, contentColor = TextMain) {
        Box(Modifier.fillMaxSize().systemBarsPadding()) {
            if (screen == SCREEN_SETTINGS) {
                BackHandler { screen = SCREEN_HOME }
                SettingsScreen(resumeTick = resumeTick, onBack = { screen = SCREEN_HOME })
            } else {
                HomeScreen(resumeTick = resumeTick, onOpenSettings = { screen = SCREEN_SETTINGS })
            }
        }
    }
}
