package com.cursokotlin.petly

import android.app.Application
import com.cursokotlin.petly.core.di.AppContainer

class PetlyApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}