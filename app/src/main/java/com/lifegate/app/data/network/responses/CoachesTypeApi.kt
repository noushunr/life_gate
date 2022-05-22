package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachesTypeApi {

    data class CoachesTypeResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<CoachesType>? = null

    )

    data class CoachesType(

        @SerializedName("coach_type_id")
        var coach_type_id: String? = null,

        @SerializedName("type")
        var type: String? = null,

        @SerializedName("type_icon")
        var type_icon: String? = null,

        @SerializedName("type_banner")
        var type_banner: String? = null

    )

}