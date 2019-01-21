package com.cleverapp.ui.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

/**
 * Default implementation of {@link androidx.navigation.fragment.NavHostFragment} uses
 * {@link FragmentNavigator} which replaces existing fragments when adds a new one to a backstack.
 * This provides {@link FragmentManager} which {@link FragmentTransaction} executes add instead
 * of replace.
 */
@Navigator.Name("fragment")
public class AppFragmentNavigator extends FragmentNavigator {

    public AppFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, new AppFragmentManager(manager), containerId);
    }

    private static class AppTransaction extends FragmentTransaction {

        FragmentTransaction host;

        AppTransaction(FragmentTransaction host) {
            this.host = host;
        }

        @NonNull
        @Override
        public FragmentTransaction add(@NonNull Fragment fragment, @Nullable String tag) {
            return host.add(fragment, tag);
        }

        @NonNull
        @Override
        public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment) {
            return host.add(containerViewId, fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction add(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
            return host.add(containerViewId, fragment, tag);
        }

        @NonNull
        @Override
        public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment) {
            return host.add(containerViewId, fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction replace(int containerViewId, @NonNull Fragment fragment, @Nullable String tag) {
            return host.add(containerViewId, fragment, tag);
        }

        @NonNull
        @Override
        public FragmentTransaction remove(@NonNull Fragment fragment) {
            return host.remove(fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction hide(@NonNull Fragment fragment) {
            return host.hide(fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction show(@NonNull Fragment fragment) {
            return host.show(fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction detach(@NonNull Fragment fragment) {
            return host.detach(fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction attach(@NonNull Fragment fragment) {
            return host.attach(fragment);
        }

        @NonNull
        @Override
        public FragmentTransaction setPrimaryNavigationFragment(@Nullable Fragment fragment) {
            return host.setPrimaryNavigationFragment(fragment);
        }

        @Override
        public boolean isEmpty() {
            return host.isEmpty();
        }

        @NonNull
        @Override
        public FragmentTransaction setCustomAnimations(int enter, int exit) {
            return host.setCustomAnimations(enter, exit);
        }

        @NonNull
        @Override
        public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
            return host.setCustomAnimations(enter, exit, popEnter, popExit);
        }

        @NonNull
        @Override
        public FragmentTransaction addSharedElement(@NonNull View sharedElement, @NonNull String name) {
            return host.addSharedElement(sharedElement, name);
        }

        @NonNull
        @Override
        public FragmentTransaction setTransition(int transit) {
            return host.setTransition(transit);
        }

        @NonNull
        @Override
        public FragmentTransaction setTransitionStyle(int styleRes) {
            return host.setTransitionStyle(styleRes);
        }

        @NonNull
        @Override
        public FragmentTransaction addToBackStack(@Nullable String name) {
            return host.addToBackStack(name);
        }

        @Override
        public boolean isAddToBackStackAllowed() {
            return host.isAddToBackStackAllowed();
        }

        @NonNull
        @Override
        public FragmentTransaction disallowAddToBackStack() {
            return host.disallowAddToBackStack();
        }

        @NonNull
        @Override
        public FragmentTransaction setBreadCrumbTitle(int res) {
            return host.setBreadCrumbTitle(res);
        }

        @NonNull
        @Override
        public FragmentTransaction setBreadCrumbTitle(@Nullable CharSequence text) {
            return host.setBreadCrumbTitle(text);
        }

        @NonNull
        @Override
        public FragmentTransaction setBreadCrumbShortTitle(int res) {
            return host.setBreadCrumbShortTitle(res);
        }

        @NonNull
        @Override
        public FragmentTransaction setBreadCrumbShortTitle(@Nullable CharSequence text) {
            return host.setBreadCrumbShortTitle(text);
        }

        @NonNull
        @Override
        public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) {
            return host.setReorderingAllowed(reorderingAllowed);
        }

        @Override
        public FragmentTransaction setAllowOptimization(boolean allowOptimization) {
            return host.setAllowOptimization(allowOptimization);
        }

        @NonNull
        @Override
        public FragmentTransaction runOnCommit(@NonNull Runnable runnable) {
            return host.runOnCommit(runnable);
        }

        @Override
        public int commit() {
            return host.commit();
        }

        @Override
        public int commitAllowingStateLoss() {
            return host.commitAllowingStateLoss();
        }

        @Override
        public void commitNow() {
            host.commitNow();
        }

        @Override
        public void commitNowAllowingStateLoss() {
            host.commitNowAllowingStateLoss();
        }
    }


    private static class AppFragmentManager extends FragmentManager {

        FragmentManager host;

        AppFragmentManager(FragmentManager host) {
            this.host = host;
        }

        @NonNull
        @Override
        public FragmentTransaction beginTransaction() {
            return new AppTransaction(host.beginTransaction());
        }

        @Override
        public boolean executePendingTransactions() {
            return host.executePendingTransactions();
        }

        @Nullable
        @Override
        public Fragment findFragmentById(int id) {
            return host.findFragmentById(id);
        }

        @Nullable
        @Override
        public Fragment findFragmentByTag(@Nullable String tag) {
            return host.findFragmentByTag(tag);
        }

        @Override
        public void popBackStack() {
            host.popBackStack();
        }

        @Override
        public boolean popBackStackImmediate() {
            return host.popBackStackImmediate();
        }

        @Override
        public void popBackStack(@Nullable String name, int flags) {
            host.popBackStack(name, flags);
        }

        @Override
        public boolean popBackStackImmediate(@Nullable String name, int flags) {
            return host.popBackStackImmediate(name, flags);
        }

        @Override
        public void popBackStack(int id, int flags) {
            host.popBackStack(id, flags);
        }

        @Override
        public boolean popBackStackImmediate(int id, int flags) {
            return host.popBackStackImmediate(id, flags);
        }

        @Override
        public int getBackStackEntryCount() {
            return host.getBackStackEntryCount();
        }

        @NonNull
        @Override
        public BackStackEntry getBackStackEntryAt(int index) {
            return host.getBackStackEntryAt(index);
        }

        @Override
        public void addOnBackStackChangedListener(@NonNull OnBackStackChangedListener listener) {
            host.addOnBackStackChangedListener(listener);
        }

        @Override
        public void removeOnBackStackChangedListener(@NonNull OnBackStackChangedListener listener) {
            host.removeOnBackStackChangedListener(listener);
        }

        @Override
        public void putFragment(@NonNull Bundle bundle, @NonNull String key, @NonNull Fragment fragment) {
            host.putFragment(bundle, key, fragment);
        }

        @Nullable
        @Override
        public Fragment getFragment(@NonNull Bundle bundle, @NonNull String key) {
            return host.getFragment(bundle,key);
        }

        @NonNull
        @Override
        public List<Fragment> getFragments() {
            return host.getFragments();
        }

        @Nullable
        @Override
        public Fragment.SavedState saveFragmentInstanceState(Fragment f) {
            return host.saveFragmentInstanceState(f);
        }

        @Override
        public boolean isDestroyed() {
            return host.isDestroyed();
        }

        @Override
        public void registerFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks cb, boolean recursive) {
            host.registerFragmentLifecycleCallbacks(cb, recursive);
        }

        @Override
        public void unregisterFragmentLifecycleCallbacks(@NonNull FragmentLifecycleCallbacks cb) {
            host.unregisterFragmentLifecycleCallbacks(cb);
        }

        @Nullable
        @Override
        public Fragment getPrimaryNavigationFragment() {
            return host.getPrimaryNavigationFragment();
        }

        @Override
        public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            host.dump(prefix, fd, writer, args);
        }

        @Override
        public boolean isStateSaved() {
            return host.isStateSaved();
        }
    }

}
