package com.sample.mvvm.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding架構, 設定好相依Layout並實作getViewBinding()以取得binding物件
 * @author Jack.su
 */
abstract class BaseBindingFragment<B : ViewBinding> : Fragment() {
    private var _binding: B? = null
    protected val binding get() = _binding!!

    // 實作ViewBinding
    abstract fun getViewBinding(viewGroup: ViewGroup?): B

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getViewBinding(viewGroup)
        super.onCreateView(inflater, viewGroup, savedInstanceState)
        return binding.root
    }

    // fix memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}