/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.tasks

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.TasksActBinding
import com.google.android.material.navigation.NavigationView

/**
 * Main activity for the todoapp. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TasksActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: TasksActBinding

    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up 'core-splashscreen' to handle the splash screen in a backward compatible manner.
        splashScreen = installSplashScreen()

        // The splash screen remains on the screen as long as this condition is true.
        // splashScreen.setKeepOnScreenCondition { !viewModel.isReady }
        splashScreen.setKeepOnScreenCondition { true }

        // Configure edge-to-edge display.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = TasksActBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)

        // This callback is called when the app is ready to draw its content and replace the splash
        // screen. We can customize the exit animation of the splash screen here.
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->

            // The animated vector drawable is already animating at this point. Depending on the
            // duration of the app launch, the animation might not have finished yet.
            // Check the extension property to see how to calculate the remaining duration of the
            // icon animation.
            val remainingDuration = splashScreenViewProvider.iconAnimationRemainingDurationMillis

            // The callback gives us a `SplashScreenViewProvider` as its parameter. It holds the
            // view for the entire splash screen.
            val splashScreenView = splashScreenViewProvider.view
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            // Make sure to call SplashScreenViewProvider.remove at the end of your custom
            // animation.
            slideUp.doOnEnd { splashScreenViewProvider.remove() }

            // For the purpose of the demo, we wait for the icon animation to finish. Your app
            // should prioritize showing app content as soon as possible.
            slideUp.startDelay = remainingDuration
            slideUp.start()
        }

        val navController = findNavController(R.id.nav_host_fragment)

        // appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        appBarConfiguration = AppBarConfiguration.Builder(R.id.tasks_fragment_dest, R.id.statistics_fragment_dest)
                .setOpenableLayout(drawerLayout)
                .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

        splashScreen.setKeepOnScreenCondition { false }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }

    /**
     * Calculates the remaining duration of the icon animation based on the total duration
     * ([SplashScreenViewProvider.iconAnimationDurationMillis]) and the start time
     * ([SplashScreenViewProvider.iconAnimationStartMillis])
     */
    private val SplashScreenViewProvider.iconAnimationRemainingDurationMillis: Long
        get() {
            return iconAnimationDurationMillis - (System.currentTimeMillis() - iconAnimationStartMillis)
                .coerceAtLeast(0L)
        }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
