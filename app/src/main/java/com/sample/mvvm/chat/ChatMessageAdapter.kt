package com.sample.mvvm.chat

import android.graphics.Bitmap
import android.util.LruCache
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mitake.finance.sqlite.util.SharePreferenceManager
import com.mitake.function.BR
import com.mitake.function.R
import com.mitake.function.binding.BaseBindingViewHolder
import com.mitake.function.chat.ChatMessageViewModel
import com.mitake.function.`object`.LoadImage
import com.mitake.variable.`object`.im.ForumNewsReplyData
import com.mitake.variable.utility.UICalculator
import com.sample.mvvm.binding.BaseBindingAdapter
import com.sample.mvvm.binding.BaseBindingViewHolder
import com.sample.mvvm.databinding.ItemChatMessageBinding
import java.io.File

class ChatMessageAdapter(val callback: ChatMessageAdapterCallback) : BaseBindingAdapter<ForumNewsReplyData?, ItemChatMessageBinding>() {
    companion object {
        private val TAG = this::class.java.simpleName
        private const val DEBUG = false
    }

    override fun getViewBinding(): (LayoutInflater, ViewGroup) -> ItemChatMessageBinding = { layoutInflater, parent ->
        ItemChatMessageBinding.inflate(layoutInflater, parent, false)
    }

    override fun setViewHolder(binding: ItemChatMessageBinding): BaseBindingViewHolder<ForumNewsReplyData?> {
        return ViewHolder(binding)
    }

    private inner class ViewHolder<T>(val binding: ItemChatMessageBinding) : BaseBindingViewHolder<T>(binding), LoadImage.LoadImageCallback {
        override fun bind(item: T) {
            binding.setVariable(BR.viewModel, callback.getViewModel())
            binding.setVariable(BR.loadImageCallback, this)
            super.bind(item)
        }

        override fun getSharePreferenceManager() = callback.getSharePreferenceManager()
        override fun getLruCache(): LruCache<String?, Bitmap> = callback.getLruCache()
        override fun getKey(): String = binding.data?.forumReply?.authorId?.toString() ?: ""
        override fun getUpdateTime(): String = binding.data?.forumReply?.timestamp ?: ""
        override fun getUrl(): String = binding.data?.forumReply?.authorPic?.split('|')?.first() ?: ""
        override fun getDefaultIcon(): Int = R.drawable.bk_navibar_profilepic_n
        override fun getFileStreamPath() = callback.getFileStreamPath()
        override fun getPathName(): String = "${callback.getFileStreamPath()}/${getKey()}.png"
        override fun putBitmap(id: String?, time: String?, bitmap: Bitmap?) {
            getSharePreferenceManager().putString(id + "_version", time)
            getLruCache().put(id, bitmap)
        }

        override fun setBitmap(id: String?, bitmap: Bitmap?) {
            binding.apply {
                if (icon.tag != null && id == icon.tag) {
                    icon.setImageBitmap(bitmap)
                }
            }
        }

        init {
            binding.apply {
                val context = root.context
                root.setOnClickListener {
                    callback.onClick()
                }
                root.layoutParams.height = (UICalculator.getHeight(context) / 10).toInt()
                icon.layoutParams.height = (UICalculator.getHeight(context) / 17).toInt()
                icon.layoutParams.width = icon.layoutParams.height
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, UICalculator.getRatioWidth(context, 10))
                content.setTextSize(TypedValue.COMPLEX_UNIT_PX, UICalculator.getRatioWidth(context, 16))
            }
        }
    }
}

interface ChatMessageAdapterCallback {
    fun getViewModel(): ChatMessageViewModel
    fun getSharePreferenceManager(): SharePreferenceManager
    fun getLruCache(): LruCache<String?, Bitmap>
    fun getFileStreamPath(): File
    fun onClick()
}