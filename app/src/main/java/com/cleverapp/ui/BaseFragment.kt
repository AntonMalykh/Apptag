package com.cleverapp.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected val tagService by lazy { (activity as MainActivity).tagService }

    fun <T : View> Fragment.findView(@IdRes resId : Int) : T {
        return this.activity!!.findViewById(resId) as T
    }
}