package com.sendbird.searchbook.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResGetBookList(
    @SerializedName("total")
    @Expose
    val total: String,

    @SerializedName("page")
    @Expose
    val page: String,

    @SerializedName("books")
    @Expose
    val books: List<Book> = emptyList()
)
