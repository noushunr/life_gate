package com.lifegate.app.ui.fragments.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.UserRepository


/*
 *Created by Adithya T Raj on 25-06-2021
*/

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(repository) as T
    }

}