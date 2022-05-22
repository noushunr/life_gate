package com.lifegate.app.ui.fragments.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.UserRepository


/*
 *Created by Adithya T Raj on 24-06-2021
*/

@Suppress("UNCHECKED_CAST")
class OnBoardViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OnBoardViewModel(repository) as T
    }

}