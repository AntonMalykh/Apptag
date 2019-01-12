package com.cleverapp.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.ui.viewmodels.ViewModelFactory

abstract class BaseFragment : Fragment(){

    protected val navController: NavController
        get() {
            return NavHostFragment.findNavController(this)
        }


    protected fun <T: ViewModel> getViewModel(viewModelClass: Class<T>): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
            activity?.let {
                ViewModelProviders.of(
                        this,
                        ViewModelFactory(it.application)).get(viewModelClass)
            } ?: throw IllegalStateException("Invalid activity (null)")
        }}
}