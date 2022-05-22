package com.lifegate.app.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.data.network.responses.GraphApi
import com.lifegate.app.data.network.responses.ProfileApi
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    private val mutableData = MutableLiveData<ProfileApi.ProfileData>()
    private val mutableGraph = MutableLiveData<MutableList<GraphApi.GraphData>>()
    var filePaths : MutableList<String> = mutableListOf()
    var filePathsEquipment : MutableList<String> = mutableListOf()

    var countryName = ""
    var countryId = ""

    val liveData : LiveData<ProfileApi.ProfileData>
        get() = mutableData
    val liveGraph : LiveData<MutableList<GraphApi.GraphData>>
        get() = mutableGraph


    fun fetchProfile() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userProfile()

                checkProfileResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : ProfileApi.ProfileResponse = fromJson(error.message.toString())

                    checkProfileResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                listener?.onFailure()
            }
        }

    }

    private fun checkProfileResponse(response : ProfileApi.ProfileResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            errorMessage = response.message.toString()
            mutableData.value = data!!
            repository.saveProfileData(data)
            countryId = data.user_country.toString()

            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        fetchChart()

        println(response.toString())
    }

    fun fetchChart() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userGetGraph()

                checkGraphResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : GraphApi.GraphResponse = fromJson(error.message.toString())

                    checkGraphResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                listener?.onFailure()
            }
        }

    }

    private fun checkGraphResponse(response : GraphApi.GraphResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            errorMessage = response.message.toString()
            mutableGraph.value = data!!

            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        fetchCountry()

        println(response.toString())
    }


    fun updateProfile(
        fullname: String?,
        email: String?,
        mobile: String?,
        weight: String?,
        gender: String?,
        birthday: String?,
        interest: String?,
        goal: String?,
        weight_goal: String?,
        fitness_level: String?,
        allergies: String?,
        notes: String?,
        available_days: String?
    ) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                builder.addFormDataPart("fullname", fullname)
                builder.addFormDataPart("email", email)
                builder.addFormDataPart("mobile", mobile)
                builder.addFormDataPart("weight", weight)
                builder.addFormDataPart("gender", gender)
                builder.addFormDataPart("birthday", birthday)
                builder.addFormDataPart("interest", interest)
                builder.addFormDataPart("goal", goal)
                builder.addFormDataPart("weight_goal", weight_goal)
                builder.addFormDataPart("fitness_level", fitness_level)
                builder.addFormDataPart("allergies", allergies)
                builder.addFormDataPart("notes", notes)
                builder.addFormDataPart("available_days", available_days)

                if (filePaths.isNotEmpty()) {
                    for (i in filePaths) {
                        val file = File(i)
                        val requestBody: RequestBody =
                            RequestBody.create(MediaType.parse("image/jpeg"), file)
                        builder.addFormDataPart("injuries", file.name, requestBody)
                    }
                }
                if (filePathsEquipment.isNotEmpty()) {
                    for (i in filePathsEquipment) {
                        val file = File(i)
                        val requestBody: RequestBody =
                            RequestBody.create(MediaType.parse("image/jpeg"), file)
                        builder.addFormDataPart("equipments", file.name, requestBody)
                    }
                }
                val requestBody = builder.build()

                val response = repository.userProfileUpdate(
                    requestBody
                )

                checkUpdateProfileResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : ProfileApi.ProfileResponse = fromJson(error.message.toString())

                    checkUpdateProfileResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                listener?.onFailure()
            }
        }

    }

    private fun checkUpdateProfileResponse(response : ProfileApi.ProfileResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            errorMessage = response.message.toString()
            listener?.onFailure()

            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        fetchProfile()

        println(response.toString())
    }

    fun fetchCountry() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAllCountries()

                checkCountryResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CountriesApi.CountriesResponse = fromJson(error.message.toString())

                    checkCountryResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkCountryResponse(response : CountriesApi.CountriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            updateCountryName(data)
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    private fun updateCountryName(countryList: MutableList<CountriesApi.CountriesData>) {
        for (country in countryList) {
            if (country.id.equals(countryId)) {
                countryName = country.name.toString()
                break
            }
        }
    }

}