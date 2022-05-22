package com.lifegate.app.ui.fragments.splash

import androidx.lifecycle.ViewModel
import com.lifegate.app.data.repositories.UserRepository

class SplashViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    fun getUserSelectedCity() = repository.getUserSelectedCity()

    fun getUserLatitude() = repository.getUserLatitude()
    fun getUserLongitude() = repository.getUserLongitude()

}