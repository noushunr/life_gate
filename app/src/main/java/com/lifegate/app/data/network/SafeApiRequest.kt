package com.lifegate.app.data.network

import com.lifegate.app.MainApplication
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.utils.ApiException
import com.lifegate.app.utils.ErrorBodyException
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

/*
 *Created by Adithya T Raj on 24-06-2021
*/

abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            Timber.tag("response").e(response.body().toString())
            return response.body()!!
        } else if(response.code() == 401) {
            val prefs = PreferenceProvider(MainApplication.appContext)
            prefs.saveLoginStatus(false)
            var error = response.errorBody()?.use {
                it.string()
            }
            Timber.tag("error").e(error)
            if (response.body() != null) {
                error = Gson().toJson(response.body())
                Timber.tag("response").e(error)
            }
            throw ErrorBodyException(error)
        } else if(response.code() == 401 || response.code() == 400 || response.code() == 100 || response.code() == 404) {
            var error = response.errorBody()?.use {
                it.string()
            }
            Timber.tag("error").e(error)
            if (response.body() != null) {
                error = Gson().toJson(response.body())
                Timber.tag("response").e(error)
            }
            throw ErrorBodyException(error)
        } else {
            val error = response.errorBody()?.use {
                it.string()
            }

            val message = StringBuilder()
            error?.let {
                try {
                    message.append(JSONObject(it).getString("message"))
                } catch (e: JSONException) {
                }
                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")
            throw ApiException(message.toString())
        }
    }

}