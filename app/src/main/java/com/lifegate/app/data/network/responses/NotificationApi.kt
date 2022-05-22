package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class NotificationApi {

    data class NotificationResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<NotificationData>? = null

    )

    data class NotificationData(

        @SerializedName("notification_title")
        var notification_title: String? = null,

        @SerializedName("notification_content")
        var notification_content: String? = null,

        @SerializedName("notification_date")
        var notification_date: String? = null

    )

}