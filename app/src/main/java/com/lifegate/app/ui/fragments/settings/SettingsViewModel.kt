package com.lifegate.app.ui.fragments.settings

import androidx.lifecycle.ViewModel
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.utils.NetworkListener

class SettingsViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    fun getLoginStatus() = repository.getLoginStatus()

    fun logOutUser() = repository.logOutUser()

}