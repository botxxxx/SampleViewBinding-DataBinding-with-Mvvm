package com.sample.mvvm.binding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewDataBinding架構, 設定Item的資料data(T : Any?)與UI(B : ViewDataBinding)
 * 預設xml的item資料名稱為name="data"會映射到BR.data
 * 應使用submitList注入資料,DiffUtil.ItemCallback會檢查setVariable之資料是否更新
 * 如固定的參數寫在BaseBindingViewHolder的init(textSize,textColor..),變動的方法寫在viewModelFunction
 * @author Jack.su
 */
abstract class BaseDataBindingAdapter<T : Any, B : ViewDataBinding> : ListAdapter<T, BaseDataBindingViewHolder<T>>(ItemDiffCallback<T>()) {
    // 實作ViewBinding,ViewHolder
    abstract fun getViewBinding(): (LayoutInflater, ViewGroup) -> B
    abstract fun setViewHolder(binding: B): BaseDataBindingViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataBindingViewHolder<T> {
        val binding: B = getViewBinding().invoke(LayoutInflater.from(parent.context), parent)
        return setViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<T>, position: Int) {
        //防止資料為null之閃退
        if (getItem(position) != null) {
            holder.bind(getItem(position))
        }
    }

    //<T>非null但仍可輸入null資料(Binding將不生效)
    class ItemDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }
}

open class BaseDataBindingViewHolder<T>(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), ViewHolderHandler<T> {
    /**
     * 可以複寫bind加入viewModel與callback來完成更複雜的功能(ex:BR.viewModel, BR.loadImageCallback)
     */
    override fun bind(item: T) {
//        binding.setVariable(BR.data, item)
        binding.executePendingBindings()
    }
}

private interface ViewHolderHandler<T> {
    fun bind(item: T)
}