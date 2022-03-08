package com.sendbird.searchbook.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.sendbird.searchbook.base.BaseViewModel
import com.sendbird.searchbook.model.Book
import com.sendbird.searchbook.model.DataModel
import com.sendbird.searchbook.model.MainStatusData
import com.sendbird.searchbook.model.ResGetBookDetail
import com.sendbird.searchbook.setting.Constants.Companion.ITEM_NUM_PER_PAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class MainViewModel(private val dataModel: DataModel): BaseViewModel() {
    val OPERATION_OR = "|"
    val OPERATION_NOT = "-"

    private val status = MainStatusData()

    private val _listLiveData = MutableLiveData<ArrayList<Book>>(arrayListOf())
    private val _queryLiveData = MutableLiveData<String>()
    private val _keywordLeftLiveData = MutableLiveData<String>()
    private val _keywordRightLiveData = MutableLiveData<String>()
    private val _reqPageLiveData = MutableLiveData<Int>()
    private val _selectedItemLiveData = MutableLiveData<Book>()
    private val _detailLiveData = MutableLiveData<ResGetBookDetail>()
    private val _progressLiveData = MutableLiveData(View.GONE)
    private val _filteringProcessSharedFlow = MutableSharedFlow<Int>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.SUSPEND)

    val listLiveData: LiveData<ArrayList<Book>> = _listLiveData
    private val queryFlow = _queryLiveData.asFlow().distinctUntilChanged()
    private val keywordsFlow = combine(_keywordLeftLiveData.asFlow(), _keywordRightLiveData.asFlow()) { keywordLeft, keywordRight ->
        Pair(keywordLeft, keywordRight) }.distinctUntilChanged()
    private val reqPageFlow = _reqPageLiveData.asFlow().distinctUntilChanged()
    val selectedItemLiveData: LiveData<Book> = _selectedItemLiveData
    val detailLiveData: LiveData<ResGetBookDetail> = _detailLiveData
    val progressLiveData: LiveData<Int> = _progressLiveData
    private val filteringProcessSharedFlow = _filteringProcessSharedFlow.asSharedFlow().distinctUntilChanged()

    init {
        setQueryFlow()
        setReqSearchDataFlow()
        setReqPageFlow()
        setfilteringProcessSharedFlow()
    }


    private fun setQueryFlow() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            queryFlow.collect {
                if (it.isNullOrEmpty().not()) {
                    it.apply {
                        val isOR = contains(OPERATION_OR)
                        val isNOT = contains(OPERATION_NOT)

                        if (!(isOR && isNOT)) {
                            if (isOR || isNOT) {
                                status.operation = if (isOR) OPERATION_OR else OPERATION_NOT
                                val split = it.split(status.operation)

                                if (2 == split.size) {
                                    _listLiveData.value?.apply { if (size > 0) _listLiveData.postValue(arrayListOf()) }

                                    status.currentKeywordNum = 2
                                    _keywordLeftLiveData.postValue(split[0].trim())
                                    _keywordRightLiveData.postValue(split[1].trim())
                                }
                            }
                            else {
                                status.operation = ""
                                _listLiveData.value?.apply { if (size > 0) _listLiveData.postValue(arrayListOf()) }

                                status.currentKeywordNum = 1
                                _keywordLeftLiveData.postValue(it.trim())
                                _keywordRightLiveData.postValue("")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setReqSearchDataFlow() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            keywordsFlow.collect { pair ->
                pair.first.also { leftKeyword ->
                    if (leftKeyword.isEmpty().not()) {
                        runCatching {
                            _progressLiveData.postValue(View.VISIBLE)
                            dataModel.getBookList(leftKeyword)
                        }.fold({ response ->
//                            Log.d(
//                                "CHW",
//                                "pair.first, page = ${response.page}, total = ${response.total}, books.size = ${response.books.size}"
//                            )
                            _progressLiveData.postValue(View.GONE)

                            var filterNum = 0

                            when (status.operation) {
                                OPERATION_NOT -> {
                                    val list = getNotResultList(response.books, leftKeyword = leftKeyword, rightKeyword = pair.second)
                                    _listLiveData.postValue(ArrayList(list))
                                    filterNum = response.books.size - list.size
                                    status.notOperationFilteringTotal += filterNum
                                    _filteringProcessSharedFlow.emit(status.notOperationFilteringTotal / ITEM_NUM_PER_PAGE)
                                }
                                else -> {
                                    _listLiveData.postValue(ArrayList(response.books))
                                }
                            }

                            status.totalCount += response.total.toInt()

                            if (response.total.toInt() > response.books.size) {
                                response.total.toInt().also {
                                    val quotient = it / response.books.size

                                    status.leftKeywordPageRange = if (0 == it % quotient) (2..(it / response.books.size))
                                                else (2..(it / response.books.size + 1))
                                }
                            }
                        }, { throwable ->
                            _progressLiveData.postValue(View.GONE)
                            _toastLiveData.postValue(throwable.message)
                        })
                    }
                }

                if (OPERATION_OR == status.operation) {
                    pair.second.also { rightKeyword ->
                        if (rightKeyword.isEmpty().not()) {
                            runCatching {
                                _progressLiveData.postValue(View.VISIBLE)
                                dataModel.getBookList(rightKeyword)
                            }.fold({ response ->
//                                Log.d(
//                                    "CHW",
//                                    "pair.second, page = ${response.page}, total = ${response.total}, books.size = ${response.books.size}"
//                                )

                                _progressLiveData.postValue(View.GONE)
                                _listLiveData.postValue(ArrayList(response.books))
                                //                            resultList.addAll(response.books)
                                status.totalCount += response.total.toInt()

                                if (response.total.toInt() > response.books.size) {
                                    val quotient = response.total.toInt() / response.books.size

                                    response.total.toInt().also {
                                        status.rightKeywordPageRange =
                                            if (0 == response.total.toInt() % quotient) (2..(it / response.books.size))
                                            else (2..(it / response.books.size + 1))
                                    }
                                }
                            }, { throwable ->
                                _progressLiveData.postValue(View.GONE)
                                _toastLiveData.postValue(throwable.message)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun setReqPageFlow() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            reqPageFlow.collect {
//                Log.d("CHW", "reqPageFlow, page = $it, ${status.leftKeywordPageRange.first}, ${status.leftKeywordPageRange.last}")

                if (status.leftKeywordPageRange.first <= status.leftKeywordPageRange.last && status.leftKeywordPageRange.last >= it)
                    reqBookListByPage(query = _keywordLeftLiveData.value?:return@collect, page = it.toString())

                if (OPERATION_OR == status.operation) {
                    if (2 == status.currentKeywordNum) {
                        if (status.rightKeywordPageRange.first <= status.rightKeywordPageRange.last && status.rightKeywordPageRange.last >= it)
                            reqBookListByPage(
                                query = _keywordRightLiveData.value ?: return@collect,
                                page = it.toString()
                            )
                    }
                }
            }
        }
    }

    private fun getNotResultList(list: List<Book>, leftKeyword: String, rightKeyword: String) =
        list.filter { book ->
            rightKeyword.let {
                book.title.contains(leftKeyword, true) &&
                        ((book.title.contains(it, true) || book.subtitle.contains(it, true) || book.isbn13.contains(it, true)).not())
            }
        }

    fun setReqPageLiveData() {
//        Log.d("CHW", "setReqPageLiveData()")

        _reqPageLiveData.value = max(status.leftKeywordPageRange.first, status.rightKeywordPageRange.first)
    }

    private fun reqBookListByPage(query: String, page: String) = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            runCatching {
                _progressLiveData.postValue(View.VISIBLE)
                dataModel.getBookListByPage(query = query, page = page)
            }.fold({ response ->
//                Log.d("CHW", "getBookListByPage, page = ${response.page}, total = ${response.total}, books.size = ${response.books.size}")
                _progressLiveData.postValue(View.GONE)

                when (query) {
                    _keywordLeftLiveData.value -> {
                        when (status.operation) {
                            OPERATION_NOT -> {
                                val list = getNotResultList(response.books, leftKeyword = query, rightKeyword = _keywordRightLiveData.value?:"")
                                _listLiveData.postValue(_listLiveData.value.also { it?.addAll(list) })
                                val filterNum = response.books.size - list.size
                                status.notOperationFilteringTotal += filterNum
                                _filteringProcessSharedFlow.emit(status.notOperationFilteringTotal / ITEM_NUM_PER_PAGE)

//                                Log.d("CHW", "filterNum = $filterNum")
                            }
                            else -> _listLiveData.postValue(_listLiveData.value.also { it?.addAll(response.books) })
                        }

                        status.leftKeywordPageRange = (status.leftKeywordPageRange.first + 1)..status.leftKeywordPageRange.last
                    }
                    _keywordRightLiveData.value -> {
                        _listLiveData.postValue(_listLiveData.value.also { it?.addAll(response.books) })
                        status.rightKeywordPageRange = (status.rightKeywordPageRange.first + 1)..status.rightKeywordPageRange.last
                    }
                }

            }, { throwable ->
                _progressLiveData.postValue(View.GONE)
                _toastLiveData.postValue(throwable.message)
            })
        }
    }

    private fun setfilteringProcessSharedFlow() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            filteringProcessSharedFlow.collect {
//                Log.d("CHW", "filteringProcessSharedFlow, it = $it, leftKeywordPageRange.first = ${status.leftKeywordPageRange.first}")

                if (status.leftKeywordPageRange.first <= status.leftKeywordPageRange.last)
                    _reqPageLiveData.postValue(status.leftKeywordPageRange.first)
            }
        }
    }

    fun reqBookDetail(isbn13: String) = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            runCatching {
                _progressLiveData.postValue(View.VISIBLE)
                dataModel.getBookDetail(isbn13)
            }.fold({ response ->
                _progressLiveData.postValue(View.GONE)
                _detailLiveData.postValue(response)
            }, { throwable ->
                _progressLiveData.postValue(View.GONE)
                _toastLiveData.postValue(throwable.message)
            })
        }
    }

    fun setQuery(query: String) {
        _queryLiveData.value = query
    }

    fun setSelectedItem(item: Book) {
        _selectedItemLiveData.value = item
    }

    fun getNotOperationFilteringTotal() = status.notOperationFilteringTotal

    fun getTotalCount() = status.totalCount

    fun getStatus() = status


    fun initStatusData() {
        status.currentKeywordNum = 0
        status.totalCount = 0
        status.leftKeywordPageRange = 2..2
        status.rightKeywordPageRange = 2..2
        status.operation = ""
        status.notOperationFilteringTotal = 0
    }
}