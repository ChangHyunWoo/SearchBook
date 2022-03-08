package com.sendbird.searchbook.model

interface DataModel {
    suspend fun getBookList(query: String): ResGetBookList
    suspend fun getBookListByPage(query: String, page: String): ResGetBookList
    suspend fun getBookDetail(isbn13: String): ResGetBookDetail
}