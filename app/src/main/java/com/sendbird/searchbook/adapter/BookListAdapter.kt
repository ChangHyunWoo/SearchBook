package com.sendbird.searchbook.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.searchbook.BR
import com.sendbird.searchbook.databinding.ItemBookListBinding
import com.sendbird.searchbook.model.Book
import com.sendbird.searchbook.setting.Constants.Companion.ITEM_NUM_PER_PAGE
import com.sendbird.searchbook.setting.Constants.Companion.PAGING_BUFFER_ITEM_NUM
import com.sendbird.searchbook.viewmodel.MainViewModel

class BookListAdapter: ListAdapter<Book, BookListAdapter.BookListViewHolder>(DIFF_UTIL) {
    private lateinit var viewModel: MainViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder =
        BookListViewHolder(ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) =
        holder.bind(position)

    override fun onBindViewHolder(
        holder: BookListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

//        Log.d("CHW", "onBindViewHolder, position = $position, currentList.size = ${currentList.size}")

        if (currentList.size - position < PAGING_BUFFER_ITEM_NUM) {
            if (currentList.size != viewModel.getTotalCount())
                viewModel.setReqPageLiveData()
        }
    }

    fun setViewModel(viewModel: MainViewModel) {
        this.viewModel = viewModel
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book) =
                oldItem.isbn13 == newItem.isbn13

            override fun areContentsTheSame(oldItem: Book, newItem: Book) =
                oldItem == newItem
        }
    }

    inner class BookListViewHolder(private val binding: ItemBookListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.setVariable(BR.data, getItem(pos))
            binding.setVariable(BR.pos, pos)
            binding.setVariable(BR.viewModel, viewModel)
        }
    }
}