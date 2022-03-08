package com.sendbird.searchbook.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity() {
    abstract val layoutResourceId: Int

    lateinit var viewDataBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
        viewDataBinding.lifecycleOwner = this

        initView(savedInstanceState)
        setObserver()
    }

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun setObserver()
}