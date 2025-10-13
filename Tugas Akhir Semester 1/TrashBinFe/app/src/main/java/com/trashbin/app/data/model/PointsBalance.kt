package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class PointsBalance(
    @SerializedName("points") val points: Int,
    @SerializedName("equivalent_amount") val equivalentAmount: Double
)