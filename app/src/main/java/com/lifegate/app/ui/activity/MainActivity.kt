package com.lifegate.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lifegate.app.R
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.databinding.ActivityMainBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class MainActivity : AppCompatActivity(), KodeinAware {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    //private val prefs = PreferenceProvider(MainApplication.appContext)

    override val kodein by kodein()
    private val repository by instance<UserRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.main_nav_host)

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.splashFragment, R.id.homeFragment, R.id.coachesFragment, R.id.caloriesFragment,
            R.id.myPlanFragment, R.id.dietPlanFragment, R.id.loginFragment, R.id.signUpFragment,
            R.id.coachRegisterFragment, R.id.onBoardFragment, R.id.restaurantFragment,
            R.id.historyFragment, R.id.workoutHistoryFragment, R.id.nutritionDetailFragment,
            R.id.coachDetailFragment, R.id.profileFragment, R.id.planDetailFragment,
            R.id.checkoutFragment, R.id.workoutPlanFragment, R.id.featuredCoachFragment,
            R.id.featuredPlanFragment, R.id.nutritionRestaurantFragment, R.id.nutritionGroceryFragment,
            R.id.workoutDetailFragment, R.id.notificationFragment, R.id.dietListFragment,
            R.id.flowChartFragment, R.id.coachMessageFragment, R.id.settingsFragment, R.id.policyFragment)
            .build()

        visibilityNavElements(navController)

        binding.mainToolbar.toolbarAccount.setOnClickListener {
            //navController.navigate(R.id.action_global_loginFragment)
            checkLoginStatus()
            //navController.navigate(R.id.action_global_profileFragment)
        }

        binding.mainToolbar.toolbarSettings.setOnClickListener {
            //navController.navigate(R.id.action_global_coachRegisterFragment)
            navController.navigate(R.id.action_global_settingsFragment)
        }

        binding.mainToolbar.toolbarMail.setOnClickListener {
            navController.navigate(R.id.action_global_coachMessageFragment)
        }

        binding.mainToolbar.toolbarNotification.setOnClickListener {
            navController.navigate(R.id.action_global_notificationFragment)
        }
    }

    private fun setupNavControl() {

        setSupportActionBar(binding.mainToolbar.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.mainBottomNavigationView.setupWithNavController(navController)

        val userName = PreferenceProvider(this).getUserName()

    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    setViewsGone()
                }
                R.id.chooseLocationFragment -> {
                    setViewsGone()
                }
                R.id.onBoardFragment -> {
                    setViewsGone()
                }
                R.id.homeFragment -> {
                    navController.graph.startDestination = R.id.homeFragment
                    setViewsVisible()
                }
                else -> {
                    setViewsVisible()
                }
            }
        }
    }

    private fun setViewsVisible() {
        binding.mainAppbar.visibility = View.VISIBLE
        binding.mainToolbar.toolbar.visibility = View.VISIBLE
        binding.mainBottomNavigationView.visibility = View.VISIBLE
        setupNavControl()
    }

    private fun setViewsGone() {
        binding.mainAppbar.visibility = View.GONE
        binding.mainToolbar.toolbar.visibility = View.GONE
        binding.mainBottomNavigationView.visibility = View.GONE
        setupNavControl()
    }

    fun checkLoginStatus() {
        val status = repository.getLoginStatus()
        val currentFragment = navController.currentDestination?.id
        if (status) {
            if (currentFragment == R.id.profileFragment) {
                //this.toast("Logging out the user")
                //repository.saveLoginStatus(false)
                //navController.safePopBackStack()
            } else {
                navController.navigate(R.id.action_global_profileFragment)
            }
        } else {
            navController.navigate(R.id.action_global_loginFragment)
        }
    }

    fun progress(visibility: Int) {
        progressView(binding.progress, visibility)
    }

    fun selectLanguage(switchLang : String) {
        //prefs.saveLocale(switchLang)
        //prefs.saveLocaleStatus("1")
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }

    fun exitApp() {
        this.finishAffinity()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            /*if (resultCode == Activity.RESULT_OK && data != null) {
                val list = kauOnMediaPickerResult(resultCode, data)
                if (list.isNotEmpty()) {

                    val selectedImageUri = list[0].uri

                    val parcelFileDescriptor =
                        contentResolver.openFileDescriptor(selectedImageUri, "r", null) ?: return

                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val file = File(cacheDir, contentResolver.getFileName(selectedImageUri))
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                }
            }*/

            for (fragment in supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}