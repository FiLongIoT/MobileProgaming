package com.example.bluromatic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bluromatic.ui.BluromaticScreen
import com.example.bluromatic.ui.theme.BluromaticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluromaticTheme {
                BluromaticScreen()
            }
        }
    }
}
