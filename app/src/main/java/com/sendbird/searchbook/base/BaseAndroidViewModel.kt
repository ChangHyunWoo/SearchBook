package com.sendbird.searchbook.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseAndroidViewModel(application: Application): AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    protected val _toastLiveData = MutableLiveData<String>()

    val toastLiveData: LiveData<String> get() = _toastLiveData

    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}