package com.sendbird.searchbook.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    protected val _toastLiveData = MutableLiveData<String>()

    val toastLiveData: LiveData<String> get() = _toastLiveData

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}