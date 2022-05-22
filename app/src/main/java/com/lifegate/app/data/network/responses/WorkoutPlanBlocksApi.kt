package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class WorkoutPlanBlocksApi {

    data class WorkoutPlanBlocksResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: WorkoutPlanBlocksData? = null

    )

    data class WorkoutPlanBlocksData(

        @SerializedName("equipments")
        var equipments: MutableList<MutableList<EquipmentListApi.EquipmentData>>? = null,

        @SerializedName("total_time")
        var total_time: String? = null,

        @SerializedName("calories")
        var calories: String? = null

    )

    data class EquipmentBlock(

        @SerializedName("equipments_id")
        var equipments_id: String? = null,

        @SerializedName("equipments_name")
        var equipments_name: String? = null

    )

}