package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PolicyApi {

    data class PolicyResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: PolicyData? = null

    )

    data class PolicyData(

        @SerializedName("policy_id")
        var policyId: String? = null,

        @SerializedName("policy_content")
        var policyContent: String? = null,

        @SerializedName("policy_date")
        var policyDate: String? = null


    )

}