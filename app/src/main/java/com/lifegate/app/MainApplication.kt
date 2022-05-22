package com.lifegate.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.lifegate.app.data.db.AppDatabase
import com.lifegate.app.data.network.MyApi
import com.lifegate.app.data.network.NetworkConnectionInterceptor
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.data.repositories.HomeRepository
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.ui.fragments.auth.login.LoginViewModelFactory
import com.lifegate.app.ui.fragments.auth.signup.SignUpViewModelFactory
import com.lifegate.app.ui.fragments.calories.CaloriesViewModelFactory
import com.lifegate.app.ui.fragments.checkout.CheckoutViewModelFactory
import com.lifegate.app.ui.fragments.choose.ChooseLocationViewModelFactory
import com.lifegate.app.ui.fragments.coach.detail.CoachDetailViewModelFactory
import com.lifegate.app.ui.fragments.coach.list.CoachesViewModelFactory
import com.lifegate.app.ui.fragments.coach.register.CoachesRegisterViewModelFactory
import com.lifegate.app.ui.fragments.diet.DietPlanViewModelFactory
import com.lifegate.app.ui.fragments.diet.list.DietListViewModelFactory
import com.lifegate.app.ui.fragments.grocery.NutritionGroceryViewModelFactory
import com.lifegate.app.ui.fragments.history.HistoryViewModelFactory
import com.lifegate.app.ui.fragments.home.HomeViewModelFactory
import com.lifegate.app.ui.fragments.home.coach.FeaturedCoachViewModelFactory
import com.lifegate.app.ui.fragments.message.CoachMessageViewModelFactory
import com.lifegate.app.ui.fragments.myplan.MyPlanViewModelFactory
import com.lifegate.app.ui.fragments.myplan.detail.PlanDetailViewModelFactory
import com.lifegate.app.ui.fragments.notification.NotificationViewModelFactory
import com.lifegate.app.ui.fragments.nutrition.NutritionDetailViewModelFactory
import com.lifegate.app.ui.fragments.onboard.OnBoardViewModelFactory
import com.lifegate.app.ui.fragments.pricy_policy.PolicyViewModelFactory
import com.lifegate.app.ui.fragments.profile.ProfileViewModelFactory
import com.lifegate.app.ui.fragments.restaurant.RestaurantViewModelFactory
import com.lifegate.app.ui.fragments.restaurant.nutrition.NutritionRestaurantViewModelFactory
import com.lifegate.app.ui.fragments.settings.SettingsViewModelFactory
import com.lifegate.app.ui.fragments.splash.SplashViewModelFactory
import com.lifegate.app.ui.fragments.workout.WorkoutHistoryViewModelFactory
import com.lifegate.app.ui.fragments.workout.detail.WorkoutDetailViewModelFactory
import com.lifegate.app.ui.fragments.workout.flow.FlowChartViewModelFactory
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanViewModelFactory
import com.lifegate.app.utils.ThemeManager
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import timber.log.Timber


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class MainApplication : MultiDexApplication(), KodeinAware {

    companion object {
        lateinit var instance: MainApplication
        lateinit var pref: PreferenceProvider
            private set
        val appContext: Context
            get() {
                return instance.applicationContext
            }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        ThemeManager.applyTheme(ThemeManager.lightMode)
        pref = PreferenceProvider(appContext)
        FirebaseApp.initializeApp(this)
        /*PaymentConfiguration.init(
            appContext,
            "pk_test_51K0XjgAFS6gWh2ADCEZx6PJEsFpNabkjkm6CemIkoGR96Ue7TdPY8sFljJlW8RzdXMor4ktQmki53kzbUkvhMG3m001nZNnwwe"
        )*/
    }

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MainApplication))

        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance(), instance(), instance()) }
        bind() from singleton { HomeRepository(instance(), instance(), instance(), instance()) }
        bind() from singleton { CoachRepository(instance(), instance(), instance(), instance()) }
        bind() from singleton { PlanRepository(instance(), instance(), instance(), instance()) }

        bind() from provider { SplashViewModelFactory(instance()) }
        bind() from provider { ChooseLocationViewModelFactory(instance()) }
        bind() from provider { OnBoardViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { SignUpViewModelFactory(instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { ProfileViewModelFactory(instance()) }
        bind() from provider { CoachesViewModelFactory(instance()) }
        bind() from provider { CoachDetailViewModelFactory(instance()) }
        bind() from provider { FeaturedCoachViewModelFactory(instance()) }
        bind() from provider { PlanDetailViewModelFactory(instance()) }
        bind() from provider { MyPlanViewModelFactory(instance()) }
        bind() from provider { CheckoutViewModelFactory(instance()) }
        bind() from provider { DietPlanViewModelFactory(instance()) }
        bind() from provider { NutritionDetailViewModelFactory(instance()) }
        bind() from provider { WorkoutPlanViewModelFactory(instance()) }
        bind() from provider { RestaurantViewModelFactory(instance()) }
        bind() from provider { CaloriesViewModelFactory(instance()) }
        bind() from provider { HistoryViewModelFactory(instance()) }
        bind() from provider { WorkoutHistoryViewModelFactory(instance()) }
        bind() from provider { NutritionRestaurantViewModelFactory(instance()) }
        bind() from provider { NutritionGroceryViewModelFactory(instance()) }
        bind() from provider { WorkoutDetailViewModelFactory(instance()) }
        bind() from provider { NotificationViewModelFactory(instance()) }
        bind() from provider { DietListViewModelFactory(instance()) }
        bind() from provider { FlowChartViewModelFactory(instance()) }
        bind() from provider { CoachMessageViewModelFactory(instance()) }
        bind() from provider { SettingsViewModelFactory(instance()) }
        bind() from provider { CoachesRegisterViewModelFactory(instance()) }
        bind() from provider { PolicyViewModelFactory(instance()) }
    }

}