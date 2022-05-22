package com.lifegate.app.data.repositories

import android.content.Context
import com.lifegate.app.data.db.AppDatabase
import com.lifegate.app.data.network.MyApi
import com.lifegate.app.data.network.SafeApiRequest
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.utils.APP_HEADER_KEY
import okhttp3.MultipartBody


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class CoachRepository(
    val appContext: Context,
    private val api: MyApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : SafeApiRequest() {

    fun getLoginStatus() = prefs.getLoginStatus()

    suspend fun userCoachesType() : CoachesTypeApi.CoachesTypeResponse {
        return apiRequest {
            api.userCoachesType(APP_HEADER_KEY)
        }
    }

    suspend fun userCoachesList(
        search: String? = null,
        country: String? =null,
        city: String? = null,
        type: String? = null,
        action: String? = null,
        service: String? = null,
        page: String? = null,
        per_page: String? = null
    ) : CoachesListApi.CoachesListResponse {

        //val city = prefs.getUserSelectedCity().toString()
        //val city = null

        return apiRequest {
            api.userCoachesList(
                APP_HEADER_KEY,
                search,country, city, type, action, service, page, per_page
            )
        }
    }

    suspend fun userCoachDetail(
        coach: String
    ) : CoachDetailsApi.CoachDetailsResponse {

        return apiRequest {
            api.userCoachDetail(
                APP_HEADER_KEY, coach
            )
        }
    }

    suspend fun userCoachRatingList(
        coach: String
    ) : CoachRatingApi.CoachRatingListResponse {

        return apiRequest {
            api.userCoachRatingList(
                APP_HEADER_KEY, coach
            )
        }
    }

    suspend fun userCoachRatingAdd(
        coach: String,
        comment: String,
        rating: String
    ) : CoachRatingApi.CoachRatingAddResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userCoachRatingAdd(
                APP_HEADER_KEY, userToken, coach, comment, rating
            )
        }
    }

    suspend fun userCoachMessageAdd(
        coach: String,
        message: String
    ) : CoachRatingApi.CoachRatingAddResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userCoachMessageAdd(
                APP_HEADER_KEY, userToken, coach, message
            )
        }
    }

    fun getAdsCoachList() = prefs.getAdsCoachList()

    fun getAdsHome() = prefs.getAdsHome()

    fun getAdsCalories() = prefs.getAdsCalories()

    suspend fun userGetAllCoachMessage() : CoachMessageApi.CoachMessageResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userGetAllCoachMessage(
                APP_HEADER_KEY, userToken
            )
        }
    }

    suspend fun userAllCountries() : CountriesApi.CountriesResponse {

        return apiRequest {
            api.userAllCountries(
                APP_HEADER_KEY
            )
        }
    }

    suspend fun coachRegisteredCountries() : CountriesApi.CountriesResponse {

        return apiRequest {
            api.coachRegisteredCountries(
                APP_HEADER_KEY
            )
        }
    }

    suspend fun userCities(
        countryId: String
    ) : CitiesApi.CitiesResponse {

        return apiRequest {
            api.userCities(
                APP_HEADER_KEY, countryId
            )
        }
    }

    suspend fun coachServices(
    ) : CoachServicesApi.CoachServicesResponse {

        return apiRequest {
            api.coachServices(
                APP_HEADER_KEY
            )
        }
    }

    suspend fun registerCoach(
        body: MultipartBody
    ) : SignUpApi.SignUpResponse {

        return apiRequest {
            api.registerCoach(APP_HEADER_KEY, body)
        }
    }

    suspend fun userAds() : HomeAdsApi.HomeAdsResponse {
        return apiRequest {
            api.userAds(APP_HEADER_KEY)
        }
    }

}