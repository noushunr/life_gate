package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PlanRatingApi {

    data class PlanRatingAddResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null

    )

    data class PlanRatingListResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<PlanRating>? = null

    )

    data class PlanRating(

        @SerializedName("planrating_id")
        var planrating_id: String? = null,

        @SerializedName("planrating_userid")
        var planrating_userid: String? = null,

        @SerializedName("planrating_planid")
        var planrating_planid: String? = null,

        @SerializedName("planrating_comment")
        var planrating_comment: String? = null,

        @SerializedName("planrating_rating")
        var planrating_rating: String? = null,

        @SerializedName("planrating_date")
        var planrating_date: String? = null

    )

}