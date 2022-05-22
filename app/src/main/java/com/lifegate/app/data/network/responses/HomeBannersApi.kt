package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HomeBannersApi {

    data class HomeBannersResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<HomeBanners>? = null

    )

    data class HomeBanners(

        @SerializedName("homebanner_id")
        var homebanner_id: String? = null,

        @SerializedName("homebanner_title")
        var homebanner_title: String? = null,

        @SerializedName("homebanner_pic")
        var homebanner_pic: String? = null

    )

}