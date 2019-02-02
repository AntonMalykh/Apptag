package com.cleverapp.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.ui.viewmodels.ViewModelFactory

abstract class BaseFragment: Fragment() {

    abstract val viewId: Int

    private var isJustCreated: Boolean = true

    protected val navController: NavController
        get() {
            return NavHostFragment.findNavController(this)
        }

    private val globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener =
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (view!!.isLaidOut) {
                        onViewIsLaidOut()
                        view!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }                }
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isJustCreated = savedInstanceState == null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(viewId, container, false)
        view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        return view
    }

    override fun onPause() {
        super.onPause()
        isJustCreated = false
    }

    open fun onBackPressed(): Boolean = false

    open fun onTouchEvent(event: MotionEvent?) {}

    protected open fun onViewIsLaidOut() { }

    protected fun <T : ViewModel> getViewModel(viewModelClass: Class<T>): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
            activity?.let {
                ViewModelProviders.of(
                        this,
                        ViewModelFactory(it.application, arguments)).get(viewModelClass)
            } ?: throw IllegalStateException("Invalid activity (null)")
        }
    }

    protected fun isJustCreated(): Boolean {
        return isJustCreated
    }

}