package com.sendbird.searchbook.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sendbird.searchbook.R
import com.sendbird.searchbook.adapter.BookListAdapter
import com.sendbird.searchbook.base.BaseActivity
import com.sendbird.searchbook.databinding.ActivityMainBinding
import com.sendbird.searchbook.model.Book
import com.sendbird.searchbook.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    private val viewModel: MainViewModel by viewModel()

    private lateinit var behavior: BottomSheetBehavior<CardView>

    override fun initView(savedInstanceState: Bundle?) {
        viewDataBinding.view = this
        viewDataBinding.viewModel = viewModel

        behavior = BottomSheetBehavior.from(viewDataBinding.cvBottom)
        behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    override fun setObserver() {
        viewModel.toastLiveData.observe(this) { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        viewModel.selectedItemLiveData.observe(this) { viewModel.reqBookDetail(it.isbn13) }
        viewModel.detailLiveData.observe(this) { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    override fun onBackPressed() {
        when (behavior.state) {
            BottomSheetBehavior.STATE_EXPANDED,
            BottomSheetBehavior.STATE_HALF_EXPANDED ->
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else -> super.onBackPressed()
        }
    }

    fun onClickBtnQuery() {
        viewDataBinding.etQuery.text.toString().apply {
            if (length > 0) {
                viewModel.initStatusData()
                viewModel.setQuery(this.trim())
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(viewDataBinding.etQuery.windowToken, 0)
            }
        }
    }
}