package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class NutritionGroceryResApi {

    data class NutritionGroceryResResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: NutritionGroceryResData? = null

    )

    data class NutritionGroceryResData(

        @SerializedName("generation")
        var generation: NutritionGroceryResGeneration? = null

    )

    data class NutritionGroceryResGeneration(

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
        var current_set: NutritionGroceryResCurrentSet? = null

    )

    data class NutritionGroceryResCurrentSet(

        @SerializedName("dietset_id")
        var dietset_id: String? = null,

        @SerializedName("dietset_name")
        var dietset_name: String? = null,

        @SerializedName("dietset_total_vitamins")
        var dietset_total_vitamins: String? = null,

        @SerializedName("dietset_total_minarals")
        var dietset_total_minarals: String? = null,

        @SerializedName("restaurant")
        var restaurant: MutableList<NutritionRestaurant>? = null,

        @SerializedName("grocery")
        var grocery: MutableList<NutritionGrocery>? = null

    )

    data class NutritionRestaurant(

        @SerializedName("restaurant_name")
        var restaurant_name: String? = null,

        @SerializedName("restaurant_desc")
        var restaurant_desc: String? = null,

        @SerializedName("restaurant_pic")
        var restaurant_pic: String? = null

    )

    data class NutritionGrocery(

        @SerializedName("realgrocery_id")
        var realgrocery_id: String? = null,

        @SerializedName("realgrocery_diet_plan")
        var realgrocery_diet_plan: String? = null,

        @SerializedName("realgrocery_name")
        var realgrocery_name: String? = null,

        @SerializedName("realgrocery_desc")
        var realgrocery_desc: String? = null,

        @SerializedName("realgrocery_pic")
        var realgrocery_pic: String? = null,

        @SerializedName("realgrocery_addedbytype")
        var realgrocery_addedbytype: String? = null,

        @SerializedName("realgrocery_addedby")
        var realgrocery_addedby: String? = null,

        @SerializedName("realgrocery_delete_status")
        var realgrocery_delete_status: String? = null,

        @SerializedName("realgrocery_status_desable")
        var realgrocery_status_desable: String? = null,

        @SerializedName("realgrocery_date")
        var realgrocery_date: String? = null

    )

}