package com.lifegate.app.ui.fragments.pricy_policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.UserRepository


/*
 *Created by Adithya T Raj on 25-06-2021
*/

@Suppress("UNCHECKED_CAST")
class PolicyViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PolicyViewModel(repository) as T
    }

}