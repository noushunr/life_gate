package com.lifegate.app.ui.fragments.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lifegate.app.data.repositories.HomeRepository


/*
 *Created by Adithya T Raj on 25-06-2021
*/

@Suppress("UNCHECKED_CAST")
class RestaurantViewModelFactory(
    private val repository: HomeRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RestaurantViewModel(repository) as T
    }

}