package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class MyNutritionPlanApi {

    data class MyNutritionPlanResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MyNutritionPlanData? = null

    )

    data class MyNutritionPlanData(

        @SerializedName("generation")
        var generation: MyNutritionPlanGeneration? = null,

        @SerializedName("usernutritionlog_uid")
        var usernutritionlog_uid: String? = null,

        @SerializedName("usernutritionlog_setfoodid")
        var usernutritionlog_setfoodid: String? = null,

        @SerializedName("usernutritionlog_planid")
        var usernutritionlog_planid: String? = null,

        @SerializedName("usernutritionlog_purchaseid")
        var usernutritionlog_purchaseid: String? = null,

        @SerializedName("usernutritionlog_date")
        var usernutritionlog_date: String? = null,

        @SerializedName("usernutritionlog_calories")
        var usernutritionlog_calories: String? = null,

        @SerializedName("usernutritionlog_carbs")
        var usernutritionlog_carbs: String? = null,

        @SerializedName("usernutritionlog_proteins")
        var usernutritionlog_proteins: String? = null,

        @SerializedName("usernutritionlog_fat")
        var usernutritionlog_fat: String? = null,

        @SerializedName("usernutritionlog_status")
        var usernutritionlog_status: String? = null

    )

    data class MyNutritionPlanGeneration(

        @SerializedName("dgeneration_id")
        var dgeneration_id: String? = null,

        @SerializedName("dgeneration_name")
        var dgeneration_name: String? = null,

        @SerializedName("dgeneration_sets")
        var dgeneration_sets: String? = null,

        @SerializedName("dgeneration_days")
        var dgeneration_days: String? = null,

        @SerializedName("usernutrition_date")
        var usernutrition_date: String? = null,

        @SerializedName("current_set")
        var current_set: MyNutritionPlanCurrentSet? = null

    )

    data class MyNutritionPlanCurrentSet(

        @SerializedName("dietset_id")
        var dietset_id: String? = null,

        @SerializedName("dietset_name")
        var dietset_name: String? = null,

        @SerializedName("dietset_total_vitamins")
        var dietset_total_vitamins: String? = null,

        @SerializedName("dietset_total_minarals")
        var dietset_total_minarals: String? = null,

        @SerializedName("setfood")
        var setfood: MutableList<MyNutritionPlanSetFood>? = null

    )

    data class MyNutritionPlanSetFood(

        @SerializedName("setfood_id")
        var setfood_id: String? = null,

        @SerializedName("setfood_dietset")
        var setfood_dietset: String? = null,

        @SerializedName("setfood_name")
        var setfood_name: String? = null,

        @SerializedName("setfood_calories")
        var setfood_calories: String? = null,

        @SerializedName("setfood_carbs")
        var setfood_carbs: String? = null,

        @SerializedName("setfood_protien")
        var setfood_protien: String? = null,

        @SerializedName("setfood_fat")
        var setfood_fat: String? = null,

        @SerializedName("setfood_vitamins")
        var setfood_vitamins: String? = null,

        @SerializedName("setfood_minarals")
        var setfood_minarals: String? = null,

        @SerializedName("setfood_allownote")
        var setfood_allownote: String? = null,

        @SerializedName("setfood_image")
        var setfood_image: String? = null,

        @SerializedName("setfood_video")
        var setfood_video: String? = null,

        @SerializedName("setfood_prepare")
        var setfood_prepare: String? = null,

        @SerializedName("setfood_prepare_images")
        var setfood_prepare_images: String? = null,

        @SerializedName("setfood_prepare_video")
        var setfood_prepare_video: String? = null,

        @SerializedName("setfood_delete_status")
        var setfood_delete_status: String? = null,

        @SerializedName("setfood_addedbytype")
        var setfood_addedbytype: String? = null,

        @SerializedName("setfood_addedby")
        var setfood_addedby: String? = null,

        @SerializedName("setfood_date")
        var setfood_date: String? = null,

        @SerializedName("userlog")
        var userlog: MutableList<MyNutritionUserLog>? = null,

        @SerializedName("food_video_thumb")
        var foodVideoThumb: String? = null,

    )

    data class MyNutritionUserLog(

        @SerializedName("consumed")
        var consumed: String? = null,

        @SerializedName("usernutritionlog_date")
        var userworkoutlog_date: String? = null,

        @SerializedName("usernutritionlog_video")
        var userworkoutlog_video: String? = null,

        @SerializedName("usernutritionlog_notes")
        var userworkoutlog_notes: String? = null

    )

}