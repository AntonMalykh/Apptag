package com.cleverapp.ui.navigation

import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment

class AppNavHostFragment: NavHostFragment() {

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return AppFragmentNavigator(requireContext(), childFragmentManager, id)
    }
}