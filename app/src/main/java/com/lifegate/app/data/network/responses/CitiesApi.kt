package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CitiesApi {

    data class CitiesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<CitiesData>? = null

    )

    data class CitiesData(

        @SerializedName("id")
        var id: String? = null,

        @SerializedName("name")
        var name: String? = null

    )

}