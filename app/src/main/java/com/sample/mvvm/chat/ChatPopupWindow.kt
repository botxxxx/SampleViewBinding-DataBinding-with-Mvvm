package com.sample.mvvm.chat

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.mitake.function.chat.ChatMessageViewModel
import java.io.File

/**
 * 留言區
 */
class ChatPopupWindow internal constructor(context: Context?, callback: ChatPopupCallback) : View(context) {

    private val binding = ChatPopupWindowBinding.inflate(LayoutInflater.from(context), null, false)
    private lateinit var viewModel: ChatMessageViewModel
    private var chatAdapter: ChatMessageAdapter? = null

    init {
        onCreate(callback)
        onCreateView(callback)
    }

    override fun getRootView(): View = binding.root

    private fun onCreate(callback: ChatPopupCallback) {
        viewModel = ChatMessageViewModel()
        viewModel.apply {
            forumNewsDetailReplyData.observe(callback.getLifecycleOwner()) { replay ->
                binding.apply {
                    val replays = replay?.list ?: listOf()
                    tvHeader.text = resources.getString(R.string.chat_message_header_line, replays.size)
                    if (replays.isNotEmpty()) {
                        chatAdapter?.submitList(replays.toList())
                        val rvNewsChat = binding.rvChatMessage
                        rvNewsChat.isVisible = true
                    }
                    //TODO List<ForumNewsReplyData>
                }
            }
        }
    }

    private fun onCreateView(callback: ChatPopupCallback) {
        binding.apply {
            ivClose.setOnClickListener { callback.close() }
            if (chatAdapter == null) {
                chatAdapter = ChatMessageAdapter(
                    object : ChatMessageAdapterCallback {
                        override fun getViewModel(): ChatMessageViewModel = viewModel
                        override fun getSharePreferenceManager() = sharePreferenceManager
                        override fun getLruCache(): LruCache<String?, Bitmap> = callback.getLruCache()
                        override fun getFileStreamPath(): File = context.getFileStreamPath("ChatMessage")
                        override fun onClick() {
                            Log.e(TAG, "onClick")
                        }
                    })
            }
            rvChatMessage.adapter = chatAdapter
            viewModel.queryNewsDetailReplays(context, callback.getGuid())
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private const val DEBUG = false
    }
}

interface ChatPopupCallback {
    fun getGuid(): String
    fun getLruCache(): LruCache<String?, Bitmap>
    fun getLifecycleOwner(): LifecycleOwner
    fun updateChat()
    fun close()
}