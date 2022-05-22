package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachesListApi {

    data class CoachesListResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: CoachesListData? = null

    )

    data class CoachesListData(

        @SerializedName("list")
        var list: MutableList<Coaches>? = null,

        @SerializedName("currentpage")
        var currentpage: String? = null,

        @SerializedName("total")
        var total: String? = null

    )

    data class Coaches(

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

        @SerializedName("city_name")
        var city_name: String? = null,

        @SerializedName("coach_club_name")
        var coach_club_name: String? = null,

        @SerializedName("plan_starts")
        var plan_starts: String? = null,

        @SerializedName("coach_about")
        var coach_about: String? = null

    )

}