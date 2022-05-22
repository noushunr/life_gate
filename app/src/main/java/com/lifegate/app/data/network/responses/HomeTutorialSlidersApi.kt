package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HomeTutorialSlidersApi {

    data class HomeTutorialSlidersResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: HomeTutorialSlidersData? = null

    )

    data class HomeTutorialSlidersData(

        @SerializedName("tutorialvideos")
        var tutorialvideos: MutableList<HomeTutorialVideos>? = null,

        @SerializedName("sliders")
        var sliders: MutableList<HomeSliders>? = null

    )

    data class HomeTutorialVideos(

        @SerializedName("tutorialvideo_id")
        var tutorialvideo_id: String? = null,

        @SerializedName("tutorialvideo_title")
        var tutorialvideo_title: String? = null,

        @SerializedName("tutorialvideo_video")
        var tutorialvideo_video: String? = null,

        @SerializedName("tutorial_video_thumb")
        var tutorial_video_thumb: String? = null

    )

    data class HomeSliders(

        @SerializedName("homeslider_id")
        var homeslider_id: String? = null,

        @SerializedName("homeslider_title")
        var homeslider_title: String? = null,

        @SerializedName("homeslider_link")
        var homeslider_link: String? = null,

        @SerializedName("homeslider_pic")
        var homeslider_pic: String? = null

    )

}