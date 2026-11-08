package com.cursokotlin.petly.presentations

import androidx.compose.runtime.Composable
import com.cursokotlin.petly.core.di.AppContainer
import com.cursokotlin.petly.presentations.navigation.PetlyNavHost

@Composable
fun PetlyApp(container: AppContainer) {
    PetlyNavHost(container = container)
}