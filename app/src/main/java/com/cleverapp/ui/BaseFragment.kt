package com.cleverapp.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected val repository by lazy { (activity as MainActivity).repository }
}