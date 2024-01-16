package com.sample.mvvm.binding

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface.OnDismissListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * ViewBinding架構, 設定好相依Layout並實作getViewBinding()以取得binding物件
 * @author Jack.su
 */
abstract class BaseBottomSheetDialogFragment<B : ViewBinding> : BottomSheetDialogFragment() {
    companion object {
        private val TAG = this::class.java.simpleName
        private const val DEBUG = false
        private const val EXPANDED_TOP_OFFSET_IN_DP = 0
        const val OVERLAY_ALPHA = 0.408f
    }

    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected var config: Bundle? = null
    protected var isCancelOutside = true
    private var dismissListener: OnDismissListener? = null

    // 實作ViewBinding
    abstract fun getViewBinding(viewGroup: ViewGroup?): B

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.currentFocus?.clearFocus()
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getViewBinding(viewGroup)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        (dialog as? BottomSheetDialog)?.run {
            behavior.apply {
                expandedOffset = EXPANDED_TOP_OFFSET_IN_DP
                state = BottomSheetBehavior.STATE_EXPANDED
                isDraggable = false
            }
            window?.run {
                setCanceledOnTouchOutside(isCancelOutside)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, resources.displayMetrics.heightPixels)
                setDimAmount(OVERLAY_ALPHA)
                dismissListener?.let {
                    setOnDismissListener(it)
                }
            }
//            (view?.rootView as ViewGroup).viewTreeObserver.addOnGlobalFocusChangeListener { viewLostFocus, viewGetFocus ->
//                viewLostFocus.takeIf { viewGetFocus !is EditText }?.let {
//                    UiUtils.hideKeyboard(it)
//                }
//            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        config = if (arguments != null) {
            arguments
        } else {
            Bundle()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // fix memory leak
        _binding = null
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.dismissListener = onDismissListener
    }
}