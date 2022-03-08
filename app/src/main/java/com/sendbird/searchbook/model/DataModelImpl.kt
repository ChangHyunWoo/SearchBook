package com.sendbird.searchbook.model

import com.sendbird.searchbook.api.BookAPI

class DataModelImp(private val api: BookAPI): DataModel {
    override suspend fun getBookList(query: String) = api.getBookList(query)
    override suspend fun getBookListByPage(query: String, page: String) = api.getBookListByPage(query = query, page = page)
    override suspend fun getBookDetail(isbn13: String) = api.getBookDetail(isbn13)
}