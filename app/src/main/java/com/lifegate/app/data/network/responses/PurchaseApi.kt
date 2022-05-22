package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PurchaseApi {

    data class PurchaseResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: PurchaseData? = null

    )

    data class PurchaseData(

        @SerializedName("purchase_userid")
        var purchase_userid: String? = null,

        @SerializedName("purchase_planid")
        var purchase_planid: String? = null,

        @SerializedName("purchase_date")
        var purchase_date: String? = null,

        @SerializedName("purchase_time")
        var purchase_time: String? = null,

        @SerializedName("purchase_coachid")
        var purchase_coachid: String? = null,

        @SerializedName("purchase_plan_price")
        var purchase_plan_price: String? = null,

        @SerializedName("purchase_extra_price")
        var purchase_extra_price: String? = null,

        @SerializedName("purchase_tax")
        var purchase_tax: String? = null,

        @SerializedName("purchase_promo_discount")
        var purchase_promo_discount: String? = null,

        @SerializedName("purchase_total_price")
        var purchase_total_price: String? = null,

        @SerializedName("purchase_promocode")
        var purchase_promocode: String? = null,

        @SerializedName("purchase_start_date")
        var purchase_start_date: String? = null,

        @SerializedName("purchase_exp_date")
        var purchase_exp_date: String? = null,

        @SerializedName("purchase_plan_goal")
        var purchase_plan_goal: String? = null,

        @SerializedName("purchase_payment_status")
        var purchase_payment_status: String? = null,

        @SerializedName("curr_purchase_id")
        var curr_purchase_id: String? = null,

        @SerializedName("paymentIntents")
        var paymentIntents: PaymentIntentStripe? = null,

        @SerializedName("publishableKey")
        var publishableKey: String? = null

    )

    data class PaymentIntentStripe(

        @SerializedName("id")
        var id: String? = null,

        @SerializedName("client_secret")
        var client_secret: String? = null,

        @SerializedName("customer")
        var customer: String? = null,

        @SerializedName("purchase_date")
        var purchase_date: String? = null,

        @SerializedName("purchase_time")
        var purchase_time: String? = null,

        @SerializedName("purchase_coachid")
        var purchase_coachid: String? = null

    )

}