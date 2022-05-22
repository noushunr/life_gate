package com.lifegate.app.ui.fragments.coach.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.CoachRepository


/*
 *Created by Adithya T Raj on 24-06-2021
*/

@Suppress("UNCHECKED_CAST")
class CoachesViewModelFactory(
    private val repository: CoachRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CoachesViewModel(repository) as T
    }

}