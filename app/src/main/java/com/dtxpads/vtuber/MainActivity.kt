package com.dtxpads.vtuber

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.dtxpads.vtuber.ui.VTuberApp
import com.dtxpads.vtuber.ui.theme.ThemeState
import com.dtxpads.vtuber.ui.theme.VTuberTheme

class MainActivity : ComponentActivity() {

    // Naik setiap onResume agar status izin (mis. overlay) dibaca ulang setelah pengguna
    // kembali dari halaman pengaturan sistem.
    private var resumeTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // targetSdk 36 -> edge-to-edge dipaksa di Android 15+; bar sistem dibuat transparan + ikon terang.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        ThemeState.load(applicationContext)
        setContent {
            VTuberTheme {
                VTuberApp(resumeTick = resumeTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTick++
    }
}
