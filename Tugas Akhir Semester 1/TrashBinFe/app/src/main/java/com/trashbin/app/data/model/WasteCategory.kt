package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class WasteCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("base_price_per_unit") val basePricePerUnit: Double,
    @SerializedName("icon_url") val iconUrl: String?
)