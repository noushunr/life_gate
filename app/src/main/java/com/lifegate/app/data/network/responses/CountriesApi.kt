package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CountriesApi {

    data class CountriesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<CountriesData>? = null

    )

    data class CountriesData(

        @SerializedName("id")
        var id: String? = null,

        @SerializedName("coach_country")
        var coachCountry: String? = null,

        @SerializedName("name")
        var name: String? = null


    )

}