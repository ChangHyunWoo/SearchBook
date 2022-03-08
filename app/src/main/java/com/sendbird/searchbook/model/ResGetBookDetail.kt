package com.sendbird.searchbook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResGetBookDetail(
    @SerializedName("error")
    @Expose
    val error: String,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("subtitle")
    @Expose
    val subtitle: String,

    @SerializedName("authors")
    @Expose
    val authors: String,

    @SerializedName("publisher")
    @Expose
    val publisher: String,

    @SerializedName("isbn10")
    @Expose
    val isbn10: String,

    @SerializedName("isbn13")
    @Expose
    val isbn13: String,

    @SerializedName("pages")
    @Expose
    val pages: String,

    @SerializedName("year")
    @Expose
    val year: String,

    @SerializedName("rating")
    @Expose
    val rating: String,

    @SerializedName("desc")
    @Expose
    val desc: String,

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
