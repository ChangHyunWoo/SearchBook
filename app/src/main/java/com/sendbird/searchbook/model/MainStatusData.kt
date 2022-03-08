package com.sendbird.searchbook.model

data class MainStatusData(
    var currentKeywordNum: Int = 0,
    var totalCount: Int = 0,
    var leftKeywordPageRange: IntRange = 2..2,
    var rightKeywordPageRange: IntRange = 2..2,
    var operation: String = "",
    var notOperationFilteringTotal: Int = 0
)
