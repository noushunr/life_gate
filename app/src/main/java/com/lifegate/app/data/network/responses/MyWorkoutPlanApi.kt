package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class MyWorkoutPlanApi {

    data class MyWorkoutPlanResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MyWorkoutPlanData? = null

    )

    data class MyWorkoutPlanData(

        @SerializedName("generation")
        var generation: MyWorkoutPlanGeneration? = null,

        @SerializedName("userworkoutlog_uid")
        var userworkoutlog_uid: String? = null,

        @SerializedName("userworkoutlog_planid")
        var userworkoutlog_planid: String? = null,

        @SerializedName("userworkoutlog_purchaseid")
        var userworkoutlog_purchaseid: String? = null,

        @SerializedName("userworkoutlog_subtitleid")
        var userworkoutlog_subtitleid: String? = null,

        @SerializedName("userworkoutlog_date")
        var userworkoutlog_date: String? = null,

        @SerializedName("userworkoutlog_video")
        var userworkoutlog_video: String? = null,

        @SerializedName("userworkoutlog_status")
        var userworkoutlog_status: String? = null

    )

    data class MyWorkoutPlanGeneration(

        @SerializedName("generation_id")
        var generation_id: String? = null,

        @SerializedName("generation_name")
        var generation_name: String? = null,

        @SerializedName("generation_sets")
        var generation_sets: String? = null,

        @SerializedName("generation_days")
        var generation_days: String? = null,

        @SerializedName("userworkout_date")
        var userworkout_date: String? = null,

        @SerializedName("current_set")
        var current_set: MyWorkoutPlanCurrentSet? = null

    )

    data class MyWorkoutPlanCurrentSet(

        @SerializedName("workoutset_id")
        var workoutset_id: String? = null,

        @SerializedName("workoutset_name")
        var workoutset_name: String? = null,

        @SerializedName("mywplanmaintitle")
        var mywplanmaintitle: MutableList<MyWorkoutPlanMainTitle>? = null

    )

    data class MyWorkoutPlanMainTitle(

        @SerializedName("workoutmainlitle_id")
        var workoutmainlitle_id: String? = null,

        @SerializedName("workoutmainlitle_title")
        var workoutmainlitle_title: String? = null,

        @SerializedName("mywplansubtitle")
        var mywplansubtitle: MutableList<MyWorkoutPlanSubTitle>? = null

    )

    data class MyWorkoutPlanSubTitle(

        @SerializedName("workoutsublitle_id")
        var workoutsublitle_id: String? = null,

        @SerializedName("workoutsublitle_workoutset")
        var workoutsublitle_workoutset: String? = null,

        @SerializedName("workoutsublitle_mainlitle")
        var workoutsublitle_mainlitle: String? = null,

        @SerializedName("workoutsublitle_title")
        var workoutsublitle_title: String? = null,

        @SerializedName("workoutsublitle_rips")
        var workoutsublitle_rips: String? = null,

        @SerializedName("workoutsublitle_sets")
        var workoutsublitle_sets: String? = null,

        @SerializedName("workoutsublitle_equipments")
        var workoutsublitle_equipments: String? = null,

        @SerializedName("workoutsublitle_caloriesburn")
        var workoutsublitle_caloriesburn: String? = null,

        @SerializedName("workoutsublitle_pics")
        var workoutsublitle_pics: String? = null,

        @SerializedName("workoutsublitle_resttime")
        var workoutsublitle_resttime: String? = null,

        @SerializedName("workoutsublitle_use_weight")
        var workoutsublitle_use_weight: String? = null,

        @SerializedName("workoutsublitle_time")
        var workoutsublitle_time: String? = null,

        @SerializedName("workoutsublitle_allow_video")
        var workoutsublitle_allow_video: String? = null,

        @SerializedName("workoutsublitle_allow_note")
        var workoutsublitle_allow_note: String? = null,

        @SerializedName("workoutsublitle_resttime_text")
        var workoutsublitle_resttime_text: String? = null,

        @SerializedName("workoutsublitle_note")
        var workoutsublitle_note: String? = null,

        @SerializedName("workoutsublitle_delete_status")
        var workoutsublitle_delete_status: String? = null,

        @SerializedName("workoutsublitle_addedbytype")
        var workoutsublitle_addedbytype: String? = null,

        @SerializedName("workoutsublitle_addedby")
        var workoutsublitle_addedby: String? = null,

        @SerializedName("workoutsublitle_date")
        var workoutsublitle_date: String? = null,

        @SerializedName("equipments")
        var equipments: MutableList<MyWorkoutPlanEquipment>? = null,

        @SerializedName("userlog")
        var userlog: MutableList<MyWorkoutUserLog>? = null

    )

    data class MyWorkoutPlanEquipment(

        @SerializedName("equipments_id")
        var equipments_id: String? = null,

        @SerializedName("equipments_name")
        var equipments_name: String? = null

    )

    data class MyWorkoutUserLog(

        @SerializedName("burned")
        var burned: String? = null,

        @SerializedName("userworkoutlog_date")
        var userworkoutlog_date: String? = null,

        @SerializedName("userworkoutlog_video")
        var userworkoutlog_video: String? = null,

        @SerializedName("userworkoutlog_notes")
        var userworkoutlog_notes: String? = null

    )

}