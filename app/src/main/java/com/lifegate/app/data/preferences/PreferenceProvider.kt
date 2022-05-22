package com.lifegate.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.lifegate.app.utils.*
import timber.log.Timber


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    //Day-Night Mode

    fun saveDayNightMode(savedTheme: String?) {
        preference.edit().putString(
            KEY_DAY_NIGHT_MODE,
            savedTheme
        ).apply()
    }

    fun getDayNightMode(): String? {
        return preference.getString(KEY_DAY_NIGHT_MODE, ThemeManager.default)
    }

    //Login Status

    fun saveLoginStatus(savedStatus: Boolean) {
        preference.edit().putBoolean(
            KEY_LOGIN_STATUS,
            savedStatus
        ).apply()
    }

    fun getLoginStatus(): Boolean {
        return preference.getBoolean(KEY_LOGIN_STATUS, false)
    }

    //Remember Creds

    fun saveLastLoginEmail(savedEmail: String?) {
        preference.edit().putString(
            KEY_LAST_LOGIN_EMAIL,
            savedEmail
        ).apply()
    }

    fun getLastLoginEmail(): String? {
        return preference.getString(KEY_LAST_LOGIN_EMAIL, null)
    }

    fun saveLastLoginPassword(savedEmail: String?) {
        preference.edit().putString(
            KEY_LAST_LOGIN_PASSWORD,
            savedEmail
        ).apply()
    }

    fun getLastLoginPassword(): String? {
        return preference.getString(KEY_LAST_LOGIN_PASSWORD, null)
    }

    //Last Login Creds

    fun saveCustomerLoginEmail(savedEmail: String?) {
        preference.edit().putString(
            KEY_CUSTOMER_LOGIN_EMAIL,
            savedEmail
        ).apply()
    }

    fun getCustomerLoginEmail(): String? {
        return preference.getString(KEY_CUSTOMER_LOGIN_EMAIL, null)
    }

    fun saveCustomerLoginPassword(savedEmail: String?) {
        preference.edit().putString(
            KEY_CUSTOMER_LOGIN_PASSWORD,
            savedEmail
        ).apply()
    }

    fun getCustomerLoginPassword(): String? {
        return preference.getString(KEY_CUSTOMER_LOGIN_PASSWORD, null)
    }

    //User Data

    fun saveUserId(savedId: String?) {
        preference.edit().putString(
            KEY_USER_ID,
            savedId
        ).apply()
    }

    fun getUserId(): String? {
        return preference.getString(KEY_USER_ID, null)
    }

    fun saveUserName(savedName: String?) {
        preference.edit().putString(
            KEY_USER_NAME,
            savedName
        ).apply()
    }

    fun getUserName(): String? {
        return preference.getString(KEY_USER_NAME, null)
    }

    fun saveUserEmail(savedEmail: String?) {
        preference.edit().putString(
            KEY_USER_EMAIL,
            savedEmail
        ).apply()
    }

    fun getUserEmail(): String? {
        return preference.getString(KEY_USER_EMAIL, null)
    }

    fun saveUserPhone(savedPhone: String?) {
        preference.edit().putString(
            KEY_USER_PHONE,
            savedPhone
        ).apply()
    }

    fun getUserPhone(): String? {
        return preference.getString(KEY_USER_PHONE, null)
    }

    fun saveUserToken(savedId: String?) {
        preference.edit().putString(
            KEY_USER_TOKEN,
            savedId
        ).apply()
    }

    fun getUserToken(): String? {
        return preference.getString(KEY_USER_TOKEN, null)
    }

    fun saveUserSelectedCity(cityId: String?) {
        preference.edit().putString(
            KEY_USER_SELECTED_CITY_ID,
            cityId
        ).apply()
    }

    fun saveUserCurrentLatitude(latitude: String?) {
        preference.edit().putString(
            KEY_USER_CURRENT_LATITUDE,
            latitude
        ).apply()
    }

    fun saveUserCurrentLongitude(longitude: String?) {
        preference.edit().putString(
            KEY_USER_CURRENT_LONGITUDE,
            longitude
        ).apply()
    }

    fun getUserSelectedCity(): String? {
        return preference.getString(KEY_USER_SELECTED_CITY_ID, null)
    }

    fun getUserLatitude(): String? {
        return preference.getString(KEY_USER_CURRENT_LATITUDE, null)
    }

    fun getUserLongitude(): String? {
        return preference.getString(KEY_USER_CURRENT_LONGITUDE, null)
    }

    fun saveUserPic(savedId: String?) {
        preference.edit().putString(
            KEY_USER_PIC,
            savedId
        ).apply()
    }

    fun getUserPic(): String? {
        return preference.getString(KEY_USER_PIC, null)
    }

    fun saveUserCityName(savedId: String?) {
        preference.edit().putString(
            KEY_USER_CITY_NAME,
            savedId
        ).apply()
    }

    fun getUserCityName(): String? {
        return preference.getString(KEY_USER_CITY_NAME, null)
    }

    //FireBase Device Token

    fun saveFireBaseDeviceToken(savedToken: String?) {
        preference.edit().putString(
            KEY_FIRE_BASE_USER_TOKEN,
            savedToken
        ).apply()
    }

    fun getFireBaseDeviceToken(): String? {
        return preference.getString(KEY_FIRE_BASE_USER_TOKEN, FIRE_BASE_USER_TOKEN_DEFAULT)
    }

    //Locale

    fun saveLocale(savedLocale: String?) {
        preference.edit().putString(
            KEY_APP_LOCALE,
            savedLocale
        ).apply()
    }

    fun getLocale(): String? {
        return preference.getString(KEY_APP_LOCALE, APP_ENG_LOCALE)
    }

    fun saveLocaleStatus(savedLocale: String?) {
        preference.edit().putString(
            KEY_APP_LOCALE_STATUS,
            savedLocale
        ).apply()
    }

    fun getLocaleStatus(): String? {
        return preference.getString(KEY_APP_LOCALE_STATUS, "0")
    }

    //Biometrics

    fun saveBioMetricStatus(savedStatus: Boolean) {
        Timber.e("saveBioMetricStatus $savedStatus")
        preference.edit().putBoolean(
            KEY_APP_BIOMETRIC_STATUS,
            savedStatus
        ).apply()
    }

    fun getBioMetricStatus(): Boolean {
        return preference.getBoolean(KEY_APP_BIOMETRIC_STATUS, true)
    }

    fun saveBioEmail(savedStatus: String?) {
        preference.edit().putString(
            KEY_BIO_LOGIN_EMAIL,
            savedStatus
        ).apply()
    }

    fun getBioEmail(): String? {
        return preference.getString(KEY_BIO_LOGIN_EMAIL, "")
    }

    fun saveBioPassword(savedStatus: String?) {
        preference.edit().putString(
            KEY_BIO_LOGIN_PASSWORD,
            savedStatus
        ).apply()
    }

    fun getBioPassword(): String? {
        return preference.getString(KEY_BIO_LOGIN_PASSWORD, "")
    }

    //Ads

    fun saveAdsCoachList(savedStatus: String?) {
        preference.edit().putString(
            KEY_ADS_COACH,
            savedStatus
        ).apply()
    }

    fun getAdsCoachList(): String? {
        return preference.getString(KEY_ADS_COACH, "")
    }

    fun saveAdsHome(savedStatus: String?) {
        preference.edit().putString(
            KEY_ADS_HOME,
            savedStatus
        ).apply()
    }

    fun getAdsHome(): String? {
        return preference.getString(KEY_ADS_HOME, "")
    }

    fun saveAdsCalories(savedStatus: String?) {
        preference.edit().putString(
            KEY_ADS_CALORIES,
            savedStatus
        ).apply()
    }

    fun getAdsCalories(): String? {
        return preference.getString(KEY_ADS_CALORIES, "")
    }

}