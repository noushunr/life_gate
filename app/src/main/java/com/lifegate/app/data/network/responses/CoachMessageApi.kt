package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachMessageApi {

    data class CoachMessageResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<CoachMessageData>? = null

    )

    data class CoachMessageData(

        @SerializedName("message_id")
        var message_id: String? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("message_date")
        var message_date: String? = null,

        @SerializedName("plan_name")
        var plan_name: String? = null,

        @SerializedName("coach_fname")
        var coach_fname: String? = null,

        @SerializedName("coach_lname")
        var coach_lname: String? = null

    )

}