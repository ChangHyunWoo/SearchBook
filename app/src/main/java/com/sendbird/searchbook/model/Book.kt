package com.sendbird.searchbook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("subtitle")
    @Expose
    val subtitle: String,

    @SerializedName("isbn13")
    @Expose
    val isbn13: String,

    @SerializedName("price")
    @Expose
    val price: String,

    @SerializedName("image")
    @Expose
    val image: String,

    @SerializedName("url")
    @Expose
    val url: String
)
