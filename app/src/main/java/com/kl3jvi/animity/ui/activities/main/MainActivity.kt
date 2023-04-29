package com.kl3jvi.animity.ui.activities.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.azhon.appupdate.manager.DownloadManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kl3jvi.animity.BuildConfig
import com.kl3jvi.animity.R
import com.kl3jvi.animity.application.AnimityApplication.Companion.ONESIGNAL_APP_ID
import com.kl3jvi.animity.databinding.ActivityMainBinding
import com.kl3jvi.animity.utils.collect
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        /* Getting the navigation controller from the navigation host fragment. */
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        /* Used to set up the action bar with the navigation controller. */
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorites,
                R.id.navigation_explore,
                R.id.navigation_profile
            )
        )

        /* Setting up the action bar with the navigation controller. */
        setupActionBarWithNavController(navController, appBarConfiguration)
        /* Setting up the bottom navigation bar with the navigation controller. */
        /* Setting up the bottom navigation bar with the navigation controller. */
        navView.setupWithNavController(navController)
        setBottomBarVisibility()
//        checkUpdates()
    }

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        askForPermission(granted)
    }

    private fun askForPermission(granted: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (granted) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
            OneSignal.initWithContext(this.applicationContext)
            OneSignal.setAppId(ONESIGNAL_APP_ID)
        }
    }

    private fun checkUpdates() {
        val manager = DownloadManager.Builder(this).run {
            apkUrl("https://github.com/kl3jvi/animity/releases/download/v0.1.7/Animity-v1.1.2-universal-release.apk")
            apkName("animity.apk")
            smallIcon(R.mipmap.ic_launcher)
            apkVersionCode(2)
            apkVersionName("v0.0.18")
            apkSize("6.42MB")
            apkDescription("Improved searching and trying to add personalised content.")
            apkVersionCode(BuildConfig.VERSION_CODE + 1)
            build()
        }
        manager.download()
    }

    /* Hiding the bottom navigation bar. */
    private fun hideBottomNavBar() {
        binding.navView
            .animate()
            .translationY(binding.navView.height.toFloat())
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                /**
                 *
                 * Notifies the end of the animation. This callback is not invoked
                 * for animations with repeat count set to INFINITE.
                 *
                 * @param animation The animation which reached its end.
                 */
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.navView.isVisible = false
                }
            }).duration = 300
    }

    /* Showing the bottom navigation bar. */
    private fun showBottomNavBar() {
        binding.navView
            .animate()
            .translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                /**
                 *
                 * Notifies the end of the animation. This callback is not invoked
                 * for animations with repeat count set to INFINITE.
                 *
                 * @param animation The animation which reached its end.
                 */
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    binding.navView.isVisible = true
                }
            }).duration = 300
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onStart() {
        super.onStart()
        handleNetworkChanges()
    }

    private fun handleNetworkChanges() {
        collect(viewModel.isConnectedToNetwork) { isConnected ->
            binding.wrapper.isVisible = isConnected
            binding.noInternetStatus.noInternet.isVisible = !isConnected
        }
    }

    /**
     * When the destination changes, if the destination is the details fragment or the review details
     * fragment, hide the bottom nav bar, otherwise show it.
     */
    private fun setBottomBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in arrayOf(
                    R.id.navigation_details,
                    R.id.reviewDetailsFragment,
                    R.id.settingsFragment
                )
            ) {
                hideBottomNavBar()
            } else {
                showBottomNavBar()
            }
        }
    }
}
