package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class EquipmentListApi {

    data class EquipmentListResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<EquipmentData>? = null

    )

    data class EquipmentData(

        @SerializedName("equipments_id")
        var equipments_id: String? = null,

        @SerializedName("equipments_name")
        var equipments_name: String? = null

    )

}