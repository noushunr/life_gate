package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HomeAdsApi {

    data class HomeAdsResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<HomeAds>? = null

    )

    data class HomeAds(

        @SerializedName("ads_id")
        var ads_id: String? = null,

        @SerializedName("ads_title")
        var ads_title: String? = null,

        @SerializedName("ads_page")
        var ads_page: String? = null,

        @SerializedName("ads_pic")
        var ads_pic: String? = null

    )

}