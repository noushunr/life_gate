package com.lifegate.app.data.repositories

import android.content.Context
import com.lifegate.app.data.db.AppDatabase
import com.lifegate.app.data.network.MyApi
import com.lifegate.app.data.network.SafeApiRequest
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.utils.APP_HEADER_KEY
import com.lifegate.app.utils.PRO_IMG_BASE_URL
import okhttp3.MultipartBody


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class UserRepository(
    val appContext: Context,
    private val api: MyApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : SafeApiRequest() {

    fun getLoginStatus() = prefs.getLoginStatus()

    fun saveLoginStatus(boolean: Boolean) = prefs.saveLoginStatus(boolean)

    suspend fun userLogin(
        name: String,
        password: String
    ) : LoginApi.LoginResponse {
        return apiRequest {
            api.userLogin(APP_HEADER_KEY, name, password)
        }
    }

    suspend fun forgotPassword(
        name: String
    ) : LoginApi.LoginResponse {
        return apiRequest {
            api.forgotPassword(APP_HEADER_KEY, name)
        }
    }
    suspend fun addDeviceToken(
        token: String
    ) : ProfileApi.ProfileResponse {
        val userToken = prefs.getUserToken().toString()
        return apiRequest {
            api.addDeviceToken(APP_HEADER_KEY, userToken,token)
        }
    }

    fun saveFireBaseDeviceToken(token : String?) = prefs.saveFireBaseDeviceToken(token)

    fun getUserSelectedCity() = prefs.getUserSelectedCity()
    fun getUserLatitude() = prefs.getUserLatitude()

    fun getUserLongitude() = prefs.getUserLongitude()


    fun saveUserSelectedCity(city: String?) = prefs.saveUserSelectedCity(city)

    fun saveUserCurrentLatitude(latitude: String?) = prefs.saveUserCurrentLatitude(latitude)

    fun saveUserCurrentLongitude(longitude: String?) = prefs.saveUserCurrentLongitude(longitude)

    fun saveUserData(data: LoginApi.LoginDataResponse) {
        prefs.saveLoginStatus(true)
        prefs.saveUserEmail(data.email.toString())
        prefs.saveUserToken(data.token.toString())
    }

    suspend fun userSignUp(
        name: String,
        email: String,
        mobile: String,
        password: String,
        confirm: String,
        city: String
    ) : SignUpApi.SignUpResponse {

        return apiRequest {
            api.userSignUp(APP_HEADER_KEY, name, email, mobile, password, confirm, city)
        }
    }

    suspend fun userProfile() : ProfileApi.ProfileResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userProfile(
                APP_HEADER_KEY, userToken
            )
        }
    }

    fun saveProfileData(data: ProfileApi.ProfileData) {
        prefs.saveLoginStatus(true)
        prefs.saveUserEmail(data.user_mailid.toString())
        prefs.saveUserName(data.user_name.toString())
        prefs.saveUserPhone(data.user_phone.toString())
        prefs.saveUserPic(PRO_IMG_BASE_URL + data.user_pic.toString())
        prefs.saveUserCityName(data.cityname.toString())
    }

    fun logOutUser() {
        prefs.saveLoginStatus(false)
        prefs.saveUserEmail(null)
        prefs.saveUserName(null)
        prefs.saveUserPhone(null)
        prefs.saveUserPic(null)
        prefs.saveUserCityName(null)
    }

    suspend fun userProfileUpdate(
        body: MultipartBody
    ) : ProfileApi.ProfileResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userProfileUpdate(
                APP_HEADER_KEY, userToken, body
            )
        }
    }

    suspend fun userNotification() : NotificationApi.NotificationResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userNotification(
                APP_HEADER_KEY, userToken
            )
        }
    }

    fun getAdsCoachList() = prefs.getAdsCoachList()

    fun getAdsHome() = prefs.getAdsHome()

    fun getAdsCalories() = prefs.getAdsCalories()

    suspend fun userGetGraph() : GraphApi.GraphResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userGetGraph(
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

    suspend fun userCities(
        countryId: String
    ) : CitiesApi.CitiesResponse {

        return apiRequest {
            api.userCities(
                APP_HEADER_KEY, countryId
            )
        }
    }

    suspend fun policyData(
    ) : PolicyApi.PolicyResponse {

        return apiRequest {
            api.policyData(
                APP_HEADER_KEY
            )
        }
    }

}