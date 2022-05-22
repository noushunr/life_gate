package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachDetailsApi {

    data class CoachDetailsResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: CoachDetails? = null

    )

    data class CoachDetails(

        @SerializedName("coach_id")
        var coach_id: String? = null,

        @SerializedName("coach_fname")
        var coach_fname: String? = null,

        @SerializedName("coach_lname")
        var coach_lname: String? = null,

        @SerializedName("coach_email")
        var coach_email: String? = null,

        @SerializedName("coach_password")
        var coach_password: String? = null,

        @SerializedName("coach_mobile")
        var coach_mobile: String? = null,

        @SerializedName("coach_area")
        var coach_area: String? = null,

        @SerializedName("coach_country")
        var coach_country: String? = null,

        @SerializedName("coach_experience")
        var coach_experience: String? = null,

        @SerializedName("coach_type")
        var coach_type: String? = null,

        @SerializedName("coach_age")
        var coach_age: String? = null,

        @SerializedName("coach_availability")
        var coach_availability: String? = null,

        @SerializedName("coach_club")
        var coach_club: String? = null,

        @SerializedName("coach_about")
        var coach_about: String? = null,

        @SerializedName("coach_photo")
        var coach_photo: String? = null,

        @SerializedName("coach_services")
        var coach_services: MutableList<CoachServices>? = null,

        @SerializedName("coach_attachments")
        var coach_attachments: MutableList<CoachAttachment>? = null,

        @SerializedName("coach_awards")
        var coach_awards: MutableList<CoachAward>? = null,

        @SerializedName("coach_featured")
        var coach_featured: String? = null,

        @SerializedName("coach_active")
        var coach_active: String? = null,

        @SerializedName("coach_rating")
        var coach_rating: String? = null,

        @SerializedName("coach_featured_prio")
        var coach_featured_prio: String? = null,

        @SerializedName("coach_delete_status")
        var coach_delete_status: String? = null,

        @SerializedName("coach_date")
        var coach_date: String? = null,

        @SerializedName("coach_type_name")
        var coach_type_name: String? = null,

        @SerializedName("coach_club_name")
        var coach_club_name: String? = null,

        @SerializedName("city_name")
        var city_name: String? = null,

        @SerializedName("country_name")
        var country_name: String? = null,

        @SerializedName("coach_plans")
        var coach_plans: MutableList<CoachPlan>? = null

    )

    data class CoachServices(

        @SerializedName("service_id")
        var service_id: String? = null,

        @SerializedName("service")
        var service: String? = null

    )

    data class CoachAttachment(

        @SerializedName("attachment")
        var attachment: String? = null,

        @SerializedName("type")
        var type: String? = null

    )

    data class CoachAward(

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("award")
        var award: String? = null

    )

    data class CoachPlan(

        @SerializedName("plan_id")
        var plan_id: String? = null,

        @SerializedName("plan_name")
        var plan_name: String? = null,

        @SerializedName("plan_desc")
        var plan_desc: String? = null,

        @SerializedName("plan_cost")
        var plan_cost: String? = null,

        @SerializedName("plan_extracost")
        var plan_extracost: String? = null,

        @SerializedName("plan_tax")
        var plan_tax: String? = null,

        @SerializedName("plan_pics")
        var plan_pics: MutableList<String>? = null

    )

}