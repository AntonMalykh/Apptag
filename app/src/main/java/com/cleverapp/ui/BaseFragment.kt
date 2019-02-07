package com.cleverapp.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.ui.viewmodels.ViewModelFactory

abstract class BaseFragment : Fragment() {

    private companion object {
        const val REQUEST_CODE_PERMISSION = 0
    }

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
                    }
                }
            }

    private var myDestinationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDestinationId = navController.currentDestination?.id ?: 0
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSION)
            return
        permissions.forEachIndexed {
            index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED)
                onPermissionGranted(permission)
        }
    }

    open fun onBackPressed(): Boolean = false

    open fun onTouchEvent(event: MotionEvent?) {}

    protected open fun onViewIsLaidOut() {}

    protected fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestPermission(permission: String) {
        requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSION)
    }

    protected open fun onPermissionGranted(permission: String){}

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

    protected fun isNavigationAllowed(): Boolean {
        return navController.currentDestination?.id == myDestinationId ?: true
    }
}