package com.cursokotlin.petly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cursokotlin.petly.presentations.PetlyApp
import com.cursokotlin.petly.presentations.components.ComponentsShowcase
import com.cursokotlin.petly.ui.theme.PetlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val container = (application as PetlyApplication).container
        setContent {
            PetlyTheme {
                PetlyApp(container)
            }
        }
    }
}