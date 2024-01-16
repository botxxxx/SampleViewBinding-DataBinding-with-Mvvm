package com.mitake.function.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitake.appwidget.sound.CommonUtility.getMessageProperties
import com.mitake.function.`object`.LoadImage
import com.mitake.network.ICallback
import com.mitake.network.TelegramData
import com.mitake.parser.RDXParserV2
import com.mitake.variable.`object`.im.ForumNewsDetailReply

class ChatMessageViewModel : ViewModel() {
    val forumNewsDetailReplyData: MutableLiveData<ForumNewsDetailReply?> = MutableLiveData()
    val toastUtilityMessage: MutableLiveData<String> = MutableLiveData()

    fun queryNewsDetailReplays(context: Context?, guid: String) {
        RDXParserV2.getNewsDetailReplays(guid, object : ICallback {
            override fun callback(telegramData: TelegramData) {
                val replays = RDXParserV2.parseNewsDetailReplays(telegramData)
                forumNewsDetailReplyData.postValue(replays)
                if (DEBUG) {
                    Log.d(TAG, "guid:$guid replays:$replays")
                }
            }

            override fun callbackTimeout() {
                toastUtilityMessage.postValue(getMessageProperties(context).getProperty("WITH_SERVER_EXCHANGE_DATA_TIMEOUT"))
                if (DEBUG) {
                    Log.d(TAG, "replays callbackTimeout")
                }
            }
        })
    }

    fun getDrawable(image: ImageView, callback: LoadImage.LoadImageCallback): Drawable? {
        callback.apply {
            if (getUrl().isNullOrBlank()) {
                return ContextCompat.getDrawable(image.context, getDefaultIcon())
            } else {
                var bitmap: Bitmap? = null
                if (getUpdateTime() > getSharePreferenceManager().getString(getKey() + "_version", DEF_VALUE)) {
                    val params = arrayOf(getKey(), getUpdateTime(), getUrl())
                    LoadImage(callback).execute(*params) // 下載後更新UI
                } else {
                    bitmap = getLruCache().get(getKey())
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeFile(getPathName())
                        if (bitmap != null) {
                            getLruCache().put(getKey(), bitmap)
                        }
                    }
                }
                return BitmapDrawable(image.resources, bitmap)
            }
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private const val DEBUG = false
        private const val DEF_VALUE = "00000000000000"
    }
}