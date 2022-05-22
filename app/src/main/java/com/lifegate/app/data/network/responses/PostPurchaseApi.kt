package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PostPurchaseApi {

    data class PostPurchaseResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null

    )

    data class PostPurchaseData(

        @SerializedName("purchase_id")
        var purchase_id: String,

        @SerializedName("paymentIntent")
        var paymentIntent: PostPurchaseIntent

    )

    data class PostPurchaseIntent(

        @SerializedName("id")
        var id: String,

        @SerializedName("client_secret")
        var client_secret: String

    )

}