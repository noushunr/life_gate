package com.lifegate.app.ui.fragments.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.PlanRepository


/*
 *Created by Adithya T Raj on 24-06-2021
*/

@Suppress("UNCHECKED_CAST")
class NutritionGroceryViewModelFactory(
    private val repository: PlanRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NutritionGroceryViewModel(repository) as T
    }

}