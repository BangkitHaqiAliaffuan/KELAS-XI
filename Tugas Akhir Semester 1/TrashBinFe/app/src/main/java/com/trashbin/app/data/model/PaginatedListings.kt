package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class PaginatedListings(
    @SerializedName("data") val data: List<MarketplaceListing>,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int
)