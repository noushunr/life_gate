package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class RestaurantApi {

    data class RestaurantResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<Restaurant>? = null

    )

    data class Restaurant(

        @SerializedName("restaurant_id")
        var restaurant_id: String? = null,

        @SerializedName("restaurant_name")
        var restaurant_name: String? = null,

        @SerializedName("restaurant_country")
        var restaurant_country: String? = null,

        @SerializedName("country_name")
        var country_name: String? = null,

        @SerializedName("restaurant_city")
        var restaurant_city: String? = null,

        @SerializedName("city_name")
        var city_name: String? = null,

        @SerializedName("restaurant_breakfast")
        var restaurant_breakfast: String? = null,

        @SerializedName("restaurant_lunch")
        var restaurant_lunch: String? = null,

        @SerializedName("restaurant_dinner")
        var restaurant_dinner: String? = null,

        @SerializedName("breakfast")
        var breakfast: String? = null,

        @SerializedName("lunch")
        var lunch: String? = null,

        @SerializedName("dinner")
        var dinner: String? = null,

        @SerializedName("restaurant_pic")
        var restaurant_pic: String? = null

    )

}