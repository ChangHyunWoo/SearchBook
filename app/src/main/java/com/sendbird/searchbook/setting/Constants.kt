package com.sendbird.searchbook.setting

import com.sendbird.searchbook.BuildConfig


class Constants {
    companion object {
        val SERVER_HOST = BuildConfig.SERVER_HOST
        private const val SERVER_PATH = "/1.0/"
        val SERVER_URL_FULL = SERVER_HOST + SERVER_PATH

        val PAGING_BUFFER_ITEM_NUM = 30
        val ITEM_NUM_PER_PAGE = 10
    }
}