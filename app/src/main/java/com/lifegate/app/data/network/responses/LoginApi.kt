package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class LoginApi {

    data class LoginResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: LoginDataResponse? = null

    )

    data class LoginDataResponse(

        @SerializedName("full_name")
        var full_name: String? = null,

        @SerializedName("email")
        var email: String? = null,

        @SerializedName("token")
        var token: String? = null

    )

}