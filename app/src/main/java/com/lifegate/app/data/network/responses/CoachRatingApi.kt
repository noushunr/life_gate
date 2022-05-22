package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachRatingApi {

    data class CoachRatingAddResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null

    )

    data class CoachRatingListResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<CoachRating>? = null

    )

    data class CoachRating(

        @SerializedName("coachrating_id")
        var coachrating_id: String? = null,

        @SerializedName("coachrating_userid")
        var coachrating_userid: String? = null,

        @SerializedName("coachrating_coachid")
        var coachrating_coachid: String? = null,

        @SerializedName("coachrating_comment")
        var coachrating_comment: String? = null,

        @SerializedName("coachrating_rating")
        var coachrating_rating: String? = null,

        @SerializedName("coachrating_date")
        var coachrating_date: String? = null

    )

}