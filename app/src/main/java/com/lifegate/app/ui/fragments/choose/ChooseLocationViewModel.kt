package com.lifegate.app.ui.fragments.choose

import androidx.lifecycle.ViewModel
import com.lifegate.app.data.repositories.UserRepository

class ChooseLocationViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun saveUserSelectedCity(city: String?) = repository.saveUserSelectedCity(city)
    fun saveUserCurrentLatitude(latitude: String?) = repository.saveUserCurrentLatitude(latitude)
    fun saveUserCurrentLongitude(longitude: String?) = repository.saveUserCurrentLongitude(longitude)

}