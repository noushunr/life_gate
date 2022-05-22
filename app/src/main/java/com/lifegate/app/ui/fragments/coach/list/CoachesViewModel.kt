package com.lifegate.app.ui.fragments.coach.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class CoachesViewModel(
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
    var coachBannerList = mutableListOf<SlideModel>()

    var search : String? = null
    var type :  String? = null
    var city :  String? = null
    var country :  String? = null
    var action :  String? = null
    var service :  String? = null
    var page :  String? = null
    var per_page :  String? = null

    fun getAdsCoachList() = repository.getAdsCoachList()

    fun getAdsHome() = repository.getAdsHome()

    fun getAdsCalories() = repository.getAdsCalories()

    private val mutableCoachType = MutableLiveData<MutableList<CoachesTypeApi.CoachesType>>()
    private val mutableCoachList = MutableLiveData<MutableList<CoachesListApi.Coaches>>()
    private val mutableCountry = MutableLiveData<MutableList<CountriesApi.CountriesData>>()
    private val mutableCity = MutableLiveData<MutableList<CitiesApi.CitiesData>>()
    private val mutableCoachCountry = MutableLiveData<MutableList<CountriesApi.CountriesData>>()
    private val mutableCoachServices = MutableLiveData<MutableList<CoachServicesApi.ServicesData>>()
    private val mutableBannerList = MutableLiveData<MutableList<SlideModel>>()

    val liveCoachType : LiveData<MutableList<CoachesTypeApi.CoachesType>>
        get() = mutableCoachType
    val liveCoachList : LiveData<MutableList<CoachesListApi.Coaches>>
        get() = mutableCoachList
    val liveCountry : LiveData<MutableList<CountriesApi.CountriesData>>
        get() = mutableCountry

    val liveCity : LiveData<MutableList<CitiesApi.CitiesData>>
        get() = mutableCity

    val liveServices : LiveData<MutableList<CoachServicesApi.ServicesData>>
        get() = mutableCoachServices

    val liveCoachCountry : LiveData<MutableList<CountriesApi.CountriesData>>
        get() = mutableCoachCountry

    val liveBannerList : LiveData<MutableList<SlideModel>>
        get() = mutableBannerList

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

        println(response.toString())

        fetchCoachList()
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

        if (mutableCoachCountry.value == null || mutableCoachCountry.value?.isEmpty()!!)
            fetchCoachCountry()
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
        fetchCoachCountry()
    }

    fun fetchCoachCountry() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.coachRegisteredCountries()

                checkCoachCountryResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CountriesApi.CountriesResponse = fromJson(error.message.toString())

                    checkCoachCountryResponse(response)
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

    private fun checkCoachCountryResponse(response : CountriesApi.CountriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCoachCountry.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        fetchAds()
        println(response.toString())
    }

    fun fetchCity(country:String) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCities(country)

                checkCityResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CitiesApi.CitiesResponse = fromJson(error.message.toString())

                    checkCityResponse(response)
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

    private fun checkCityResponse(response : CitiesApi.CitiesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCity.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }


    fun fetchAds() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAds()

                checkAdsResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeAdsApi.HomeAdsResponse = fromJson(error.message.toString())

                    checkAdsResponse(response)
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

    private fun checkAdsResponse(response : HomeAdsApi.HomeAdsResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            if (data.isNotEmpty()) {

                setBanner(data)
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        fetchCoachServices()
        println(response.toString())

    }

    private fun setBanner(banner: MutableList<HomeAdsApi.HomeAds>) {
        if (banner.isEmpty()) {
            return
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            if (list?.ads_page.equals("Coach Listing",ignoreCase = true))
                slideList.add(SlideModel(list.ads_pic))
        }
        coachBannerList = slideList
        mutableBannerList.value = slideList
    }

    fun fetchCoachServices() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.coachServices()

                checkCoachServicesResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CountriesApi.CountriesResponse = fromJson(error.message.toString())

                    checkCoachCountryResponse(response)
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

    private fun checkCoachServicesResponse(response : CoachServicesApi.CoachServicesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCoachServices.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}