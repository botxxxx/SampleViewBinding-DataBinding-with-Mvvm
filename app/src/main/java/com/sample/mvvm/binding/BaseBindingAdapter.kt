package com.sample.mvvm.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * @author Jack.su
 */
abstract class BaseBindingAdapter<T : Any, B : ViewBinding> : RecyclerView.Adapter<BaseViewHolder>(), BaseBindingAdapterHandler<T> {
    protected var data: List<T>? = null

    override fun getItemCount(): Int = data?.size ?: 0
    abstract fun getViewBinding(): (LayoutInflater, ViewGroup) -> B
    abstract fun setViewHolder(binding: B): BaseViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding: B = getViewBinding().invoke(LayoutInflater.from(parent.context), parent)
        return setViewHolder(binding)
    }

    override fun submitList(list: List<T>?) {
        this.data = list
        notifyDataSetChanged()
    }
}

open class BaseViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

private interface BaseBindingAdapterHandler<T> {
    fun submitList(list: List<T>?)
}