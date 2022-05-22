package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HomeFeaturedCoachesApi {

    data class HomeFeaturedCoachesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<HomeFeaturedCoaches>? = null

    )

    data class HomeFeaturedCoaches(

        @SerializedName("coach_id")
        var coach_id: String? = null,

        @SerializedName("coach_fname")
        var coach_fname: String? = null,

        @SerializedName("coach_lname")
        var coach_lname: String? = null,

        @SerializedName("coach_photo")
        var coach_photo: String? = null,

        @SerializedName("coach_type_name")
        var coach_type_name: String? = null,

        @SerializedName("plan_starts")
        var plan_starts: String? = null

    )

}