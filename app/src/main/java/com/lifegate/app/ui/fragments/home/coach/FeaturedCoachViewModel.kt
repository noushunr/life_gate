package com.lifegate.app.ui.fragments.home.coach

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CitiesApi
import com.lifegate.app.data.network.responses.CoachesListApi
import com.lifegate.app.data.network.responses.CoachesTypeApi
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.ui.fragments.coach.list.CoachesListener
import com.lifegate.app.utils.*
import timber.log.Timber

class FeaturedCoachViewModel(
    private val repository: CoachRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var coachListener: CoachesListener? = null
    var errorMessage: String = ""

    var coachTypeList : MutableList<CoachesTypeApi.CoachesType> =  mutableListOf()
    var coachesList : MutableList<CoachesListApi.Coaches> =  mutableListOf()

    var search : String? = null
    var type :  String? = null
    var country :  String? = null
    var city :  String? = null
    var action :  String? = null
    var service :  String? = null
    var page :  String? = null
    var per_page :  String? = null

    private val mutableCoachType = MutableLiveData<MutableList<CoachesTypeApi.CoachesType>>()
    private val mutableCoachList = MutableLiveData<MutableList<CoachesListApi.Coaches>>()
    private val mutableCountry = MutableLiveData<MutableList<CountriesApi.CountriesData>>()

    val liveCoachType : LiveData<MutableList<CoachesTypeApi.CoachesType>>
        get() = mutableCoachType
    val liveCoachList : LiveData<MutableList<CoachesListApi.Coaches>>
        get() = mutableCoachList
    val liveCountry : LiveData<MutableList<CountriesApi.CountriesData>>
        get() = mutableCountry

    var isFirstTime = true

    fun fetchCoachType() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachesType()

                checkCoachTypeResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachesTypeApi.CoachesTypeResponse = fromJson(error.message.toString())

                    checkCoachTypeResponse(response)
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

    private fun checkCoachTypeResponse(response : CoachesTypeApi.CoachesTypeResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        coachTypeList = mutableListOf()

        if (data != null  && status != null && status) {
            coachTypeList = data
            listener?.onSuccess()
            coachListener?.onCoachesType()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        mutableCoachType.value = coachTypeList

        fetchCountry()
    }

    fun fetchCoachList() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachesList(
                    search,country, city, type, action, service, page, per_page
                )

                checkCoachListResponse(response)

                action = null

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachesListApi.CoachesListResponse = fromJson(error.message.toString())

                    checkCoachListResponse(response)
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

    private fun checkCoachListResponse(response : CoachesListApi.CoachesListResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        coachesList = mutableListOf()

        if (data != null  && status != null && status) {
            val list = data.list
            if (!list.isNullOrEmpty()) {
                coachesList = list
            }
            listener?.onSuccess()
        } else {
            coachesList = mutableListOf()
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        mutableCoachList.value = coachesList

        coachListener?.onCoachesList()

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
            mutableCountry.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}