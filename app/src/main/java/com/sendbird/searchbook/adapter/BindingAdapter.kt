package com.sendbird.searchbook.adapter

import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sendbird.searchbook.R
import com.sendbird.searchbook.model.Book
import com.sendbird.searchbook.viewmodel.MainViewModel

class BindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(imageview: ImageView, url: String?) {
            if (url.isNullOrEmpty().not()) {
                RequestOptions()
//                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
//                    .error()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform().also {
                        Glide.with(imageview.context)
                            .load(url)
//                            .centerCrop()
                            .apply(it)
                            .into(imageview)
                    }
            }
        }

        @JvmStatic
        @BindingAdapter("loadThumbnailImage")
        fun loadThumbnailImage(imageview: ImageView, url: String?) {
            if (url.isNullOrEmpty().not()) {
                Glide.with(imageview.context)
                    .load(url)
                    .thumbnail(0.1f)
                    .into(imageview)
            }
        }

        @JvmStatic
        @BindingAdapter("setBoldText")
        fun setBoldText(textView: TextView, text: String?) {
            if (text.isNullOrEmpty().not())
                textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        }

        @JvmStatic
        @BindingAdapter("list", "vm")
        fun setBookList(recyclerView: RecyclerView, dataList: ArrayList<Book>, viewModel: MainViewModel) {
            val adapter: BookListAdapter

            if (null == recyclerView.adapter) {
                adapter = BookListAdapter()
                recyclerView.adapter = adapter

                adapter.setViewModel(viewModel)
            }
            else adapter = recyclerView.adapter as BookListAdapter

            adapter.submitList(dataList.toMutableList())

//            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
//            recyclerView.smoothScrollToPosition(0)
        }
    }
}