package com.lifegate.app.data.model

import com.lifegate.app.utils.ScaleTypes


/*
 *Created by Adithya T Raj on 30-06-2021
*/

class SlideModel {

    var imageUrl: String? = null
    var imagePath: Int? = 0
    var title: String? = null
    var scaleType: ScaleTypes? = null

    constructor(imageUrl: String?, title: String? = null, scaleType: ScaleTypes?  = null) {
        this.imageUrl = imageUrl
        this.title = title
        this.scaleType = scaleType
    }

    constructor(imagePath: Int?, title: String?  = null, scaleType: ScaleTypes?  = null) {
        this.imagePath = imagePath
        this.title = title
        this.scaleType = scaleType
    }

    constructor(imageUrl: String?, scaleType: ScaleTypes?) {
        this.imageUrl = imageUrl
        this.scaleType = scaleType
    }

    constructor(imagePath: Int?, scaleType: ScaleTypes?) {
        this.imagePath = imagePath
        this.scaleType = scaleType
    }

}