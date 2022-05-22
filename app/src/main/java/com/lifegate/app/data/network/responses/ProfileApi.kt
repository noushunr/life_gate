package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class ProfileApi {

    data class ProfileResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: ProfileData? = null

    )

    data class ProfileData(

        @SerializedName("user_id")
        var user_id: String? = null,

        @SerializedName("user_mailid")
        var user_mailid: String? = null,

        @SerializedName("user_name")
        var user_name: String? = null,

        @SerializedName("user_about")
        var user_about: String? = null,

        @SerializedName("user_age")
        var user_age: String? = null,

        @SerializedName("user_type")
        var user_type: String? = null,

        @SerializedName("user_activeplan")
        var user_activeplan: String? = null,

        @SerializedName("user_planexpiry")
        var user_planexpiry: String? = null,

        @SerializedName("user_coach")
        var user_coach: String? = null,

        @SerializedName("user_phone")
        var user_phone: String? = null,

        @SerializedName("user_area")
        var user_area: String? = null,

        @SerializedName("user_country")
        var user_country: String? = null,

        @SerializedName("user_pic")
        var user_pic: String? = null,

        @SerializedName("user_weight")
        var user_weight: String? = null,

        @SerializedName("user_gender")
        var user_gender: String? = null,

        @SerializedName("user_dob")
        var user_dob: String? = null,

        @SerializedName("user_interest")
        var user_interest: String? = null,

        @SerializedName("user_goal")
        var user_goal: String? = null,

        @SerializedName("user_weight_goal")
        var user_weight_goal: String? = null,

        @SerializedName("user_fitness_level")
        var user_fitness_level: String? = null,

        @SerializedName("user_allergies")
        var user_allergies: String? = null,

        @SerializedName("user_injuries")
        var user_injuries: String? = null,

        @SerializedName("user_equipments")
        var user_equipments: String? = null,

        @SerializedName("user_notes")
        var user_notes: String? = null,

        @SerializedName("user_available_days")
        var user_available_days: String? = null,

        @SerializedName("user_delete_status")
        var user_delete_status: String? = null,

        @SerializedName("user_token")
        var user_token: String? = null,

        @SerializedName("user_status")
        var user_status: String? = null,

        @SerializedName("user_date")
        var user_date: String? = null,

        @SerializedName("cityname")
        var cityname: String? = null

    )

}