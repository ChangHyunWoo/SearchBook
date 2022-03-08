package com.sendbird.searchbook.api

import com.sendbird.searchbook.model.ResGetBookDetail
import com.sendbird.searchbook.model.ResGetBookList
import retrofit2.http.GET
import retrofit2.http.Path

interface BookAPI {
    @GET("search/{query}")
    suspend fun getBookList(
        @Path("query") query: String
    ): ResGetBookList

    @GET("search/{query}/{page}")
    suspend fun getBookListByPage(
        @Path("query") query: String,
        @Path("page") page: String
    ): ResGetBookList

    @GET("books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn13: String
    ): ResGetBookDetail
}