package com.sendbird.searchbook.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<T: ViewDataBinding>: Fragment(), ViewModelProvider.Factory {
    abstract val layoutResourceId: Int

    lateinit var viewDataBinding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutResourceId, container, false)
        viewDataBinding.lifecycleOwner = this

        initView()

        return viewDataBinding.root
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        TODO("Not yet implemented")
    }

    abstract fun initView()
}