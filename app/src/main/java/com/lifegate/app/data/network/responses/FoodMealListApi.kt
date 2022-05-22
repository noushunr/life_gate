package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class FoodMealListApi {

    data class FoodMealListResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<FoodMealListData>? = null

    )

    data class FoodMealListData(

        @SerializedName("groceries_id")
        var groceries_id: String? = null,

        @SerializedName("groceries_addedbytype")
        var groceries_addedbytype: String? = null,

        @SerializedName("groceries_addedby")
        var groceries_addedby: String? = null,

        @SerializedName("groceries_name")
        var groceries_name: String? = null,

        @SerializedName("groceries_restaurant")
        var groceries_restaurant: String? = null,

        @SerializedName("groceries_calory")
        var groceries_calory: String? = null,

        @SerializedName("groceries_price")
        var groceries_price: String? = null,

        @SerializedName("groceries_protien")
        var groceries_protien: String? = null,

        @SerializedName("groceries_carb")
        var groceries_carb: String? = null,

        @SerializedName("groceries_fat")
        var groceries_fat: String? = null,

        @SerializedName("groceries_vitamins")
        var groceries_vitamins: String? = null,

        @SerializedName("groceries_minarals")
        var groceries_minarals: String? = null,

        @SerializedName("groceries_quantity")
        var groceries_quantity: String? = null,

        @SerializedName("groceries_about")
        var groceries_about: String? = null,

        @SerializedName("groceries_pic")
        var groceries_pic: String? = null,

        @SerializedName("groceries_delete_status")
        var groceries_delete_status: String? = null,

        @SerializedName("groceries_available")
        var groceries_available: String? = null,

        @SerializedName("groceries_date")
        var groceries_date: String? = null,

        var isSelected : Boolean = false,

        var qty : Int = 1

    )

}