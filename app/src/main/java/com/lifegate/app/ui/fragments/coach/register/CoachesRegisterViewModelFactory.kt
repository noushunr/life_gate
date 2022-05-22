package com.lifegate.app.ui.fragments.coach.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.CoachRepository


/*
 *Created by Adithya T Raj on 24-06-2021
*/

@Suppress("UNCHECKED_CAST")
class CoachesRegisterViewModelFactory(
    private val repository: CoachRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CoachRegisterViewModel(repository) as T
    }

}