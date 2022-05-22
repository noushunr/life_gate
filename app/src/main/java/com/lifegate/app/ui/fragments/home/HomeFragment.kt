package com.lifegate.app.ui.fragments.home

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.databinding.HomeFragmentBinding
import com.lifegate.app.ui.fragments.myplan.MyPlanAdapter
import com.lifegate.app.utils.*
import com.google.gson.Gson
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.MyPlanApi
import com.lifegate.app.ui.fragments.diet.list.DietListFragmentDirections
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class HomeFragment : Fragment(), KodeinAware, NetworkListener, HomeListener, SliderAdapter.SliderClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: HomeViewModel
    private lateinit var navController: NavController
    private lateinit var binding: HomeFragmentBinding
    private val factory: HomeViewModelFactory by instance()

    private lateinit var coachAdapter: HomeFeaturedCoachAdapter
    private lateinit var planAdapter: HomeFeaturedPlanAdapter

    private lateinit var myPlanAdapter: MyPlanAdapter

    private var burned = 0
    private var consumed = 0
    private var burnedTarget = 0
    private var consumedTarget = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel.listener = this
        //viewModel.homeListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        //viewModel.fetchTutorialSlider()

        coachAdapter = HomeFeaturedCoachAdapter {
            println(it.coach_id)
            goToCoachDetail(it.coach_id.toString())
        }

        planAdapter = HomeFeaturedPlanAdapter {
            println(it.plan_id)
            goToPlanDetail(it.plan_id.toString())
        }

        binding.homeFeaturedCoachRecyclerview.adapter = coachAdapter

        binding.homeFeaturedPlanRecyclerview.adapter = planAdapter

        binding.homeFeaturesCoachViewAllTxt.setOnClickListener {
            goToFeaturedCoach()
        }

        binding.homeFeaturesPlanViewAllTxt.setOnClickListener {
            goToFeaturedPlan()
        }

        myPlanAdapter = MyPlanAdapter {
            println(it.plan_id)
            checkPlanType(it)
        }

        binding.userPlanRecyclerview.adapter = myPlanAdapter

        initViews()

        if (viewModel.isFirst) {
            viewModel.fetchTutorialSlider()
            viewModel.isFirst = false
        }
    }

    private fun initViews() {
        viewModel.liveSlideList.observe(viewLifecycleOwner, Observer { item ->
            val adapter = SliderAdapter(viewModel.homeSlideList, requireContext(), this)
            binding.homeImageSlider.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.scrollTimeInSec = 3
                it.setSliderAdapter(adapter)
                it.isAutoCycle = true
                it.startAutoCycle()
                it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            }
        })
        binding.homeImageSliderCard.setOnClickListener {
            Timber.e("hiii")
            goToSliderActivity()
        }
        viewModel.liveCoachList.observe(viewLifecycleOwner, Observer { item ->
            coachAdapter.submitList(viewModel.homeFeaturedCoachList)
        })
        viewModel.livePlanList.observe(viewLifecycleOwner, Observer { item ->
            planAdapter.submitList(viewModel.homeFeaturedPlanList)
        })
        viewModel.liveBannerList.observe(viewLifecycleOwner, Observer { item ->
            val adapter = SliderAdapter(viewModel.homeBannerList, requireContext())
            binding.homeBannerSliderView.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.scrollTimeInSec = 3
                it.setSliderAdapter(adapter)
                it.isAutoCycle = true
                it.startAutoCycle()
                it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            }
        })
        viewModel.liveTutorial.observe(viewLifecycleOwner, Observer { item ->
            loadImage(binding.homeHowToUseTutorialImg, viewModel.tutorialImg)
            binding.homeHowToUseTutorialTxt.text = viewModel.tutorialTxt
            binding.homeTutorialVideoCard.setOnClickListener(null)
            Timber.e(viewModel.tutorialVideoUrl)
            if (viewModel.tutorialVideoUrl.isNotBlank() || viewModel.tutorialVideoUrl.isNotEmpty()) {
                binding.homeTutorialVideoCard.setOnClickListener{
                    val action = HomeFragmentDirections.actionHomeFragmentToVideoActivity(
                        viewModel.tutorialVideoUrl
                    )
                    navController.safeNavigate(action)
                }
            }
        })

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            myPlanAdapter.submitList(viewModel.myPlanList)
        })

        viewModel.liveBurn.observe(viewLifecycleOwner, Observer { item ->

            binding.burnedGoal.text =  Html.fromHtml("Burned calories goal <br> <b> ${item.burning_goal}Cal</b>")

            try {
                burned = item.actual_burned!!.toInt()
                burnedTarget = item.burning_goal!!.toInt()
                setCalToday()
            } catch (e: Exception) {

            }
        })

        viewModel.liveConsumed.observe(viewLifecycleOwner, Observer { item ->

            binding.consumeGoal.text = Html.fromHtml(" Intake calories goal <br> <b>${item.consume_goal}Cal</b>")

            try {
                consumed = item.consumed_today!!.toInt()
                consumedTarget = item.consume_goal!!.toInt()
                setCalToday()
            } catch (e: Exception) {

            }

        })

        binding.userImg.setOnClickListener {
            val action = HomeFragmentDirections.actionGlobalProfileFragment()
            navController.safeNavigate(action)
        }

    }

    private fun goToFeaturedCoach() {
        val json = Gson().toJson(viewModel.homeFeaturedCoachList)
        val action = HomeFragmentDirections.actionHomeFragmentToFeaturedCoachFragment(

        )
        navController.safeNavigate(action)
    }

    private fun goToFeaturedPlan() {
        val json = Gson().toJson(viewModel.homeFeaturedPlanList)
        val action = HomeFragmentDirections.actionHomeFragmentToFeaturedPlanFragment(
            json
        )
        navController.safeNavigate(action)
    }

    private fun goToCoachDetail(id: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToCoachDetailFragment(
            id
        )
        navController.safeNavigate(action)
    }

    private fun goToPlanDetail(id: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToPlanDetailFragment(
            id
        )
        navController.safeNavigate(action)
    }

    private fun goToSliderActivity() {
        val action = HomeFragmentDirections.actionHomeFragmentToSliderActivity(
            Gson().toJson(viewModel.homeSlideList)
        )
        navController.safeNavigate(action)
    }

    private fun checkPlanType(plan: MyPlanApi.MyPlanData) {
        //goToWorkout(plan)
        when (plan.plan_basic_type) {
            KEY_PLAN_TYPE_NUTRITION -> goToNutrition(plan)
            KEY_PLAN_TYPE_WORKOUT -> goToWorkout(plan)
        }
    }

    private fun goToNutrition(plan: MyPlanApi.MyPlanData) {
        val action = HomeFragmentDirections.actionHomeFragmentToDietPlanFragment(
            plan.plan_id.toString(), plan.purchase_id.toString()
        )
        navController.safeNavigate(action)
    }

    private fun goToWorkout(plan: MyPlanApi.MyPlanData) {
        val action = HomeFragmentDirections.actionHomeFragmentToWorkoutPlanFragment(
            plan.plan_id.toString(), plan.purchase_id.toString()
        )
        navController.safeNavigate(action)
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        //view?.context?.toast(viewModel.errorMessage)
    }

    override fun onFailure() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    override fun onError() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    override fun onNoNetwork() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    override fun onTutorialVideo() {
        loadImage(binding.homeHowToUseTutorialImg, viewModel.tutorialImg)
        binding.homeHowToUseTutorialTxt.text = viewModel.tutorialTxt
    }

    override fun onSliders() {
        val adapter = SliderAdapter(viewModel.homeSlideList, requireContext(), this)
        binding.homeImageSlider.also {
            it.stopAutoCycle()
            it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            it.setIndicatorAnimation(IndicatorAnimationType.WORM)
            it.indicatorSelectedColor = Color.WHITE
            it.indicatorUnselectedColor = Color.GRAY
            it.scrollTimeInSec = 3
            it.setSliderAdapter(adapter)
            it.isAutoCycle = true
            it.startAutoCycle()
            it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        }
    }

    override fun onFeaturedCoach() {
        coachAdapter.submitList(viewModel.homeFeaturedCoachList)
    }

    override fun onFeaturedPlan() {
        planAdapter.submitList(viewModel.homeFeaturedPlanList)
    }

    override fun onBanner() {
        val adapter = SliderAdapter(viewModel.homeBannerList, requireContext())
        binding.homeBannerSliderView.also {
            it.stopAutoCycle()
            it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            it.setIndicatorAnimation(IndicatorAnimationType.WORM)
            it.indicatorSelectedColor = Color.WHITE
            it.indicatorUnselectedColor = Color.GRAY
            it.scrollTimeInSec = 3
            it.setSliderAdapter(adapter)
            it.isAutoCycle = true
            it.startAutoCycle()
            it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        }
    }

    private fun setCalToday() {
        val total = consumed - burned
        try {
            val consumedProgress = (consumed * 100 ) / consumedTarget
            val burnedProgress = (burned * 100 ) / burnedTarget
            binding.consumedProgress.progress = consumedProgress.toFloat()
            binding.burnedProgress.progress = burnedProgress.toFloat()

        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.getLoginStatus()) {
            binding.userDetail.visibility = View.VISIBLE
            binding.userNameTxt.text = viewModel.getUserName()
            var cityName = viewModel.getUserCityName()
            if (cityName!=null && !cityName?.equals("null"))
                binding.userCityTxt.text = viewModel.getUserCityName()
            else
                binding.userCityTxt.visibility = View.GONE
            loadCircleImage(binding.userImg, viewModel.getUserPic())
        } else {
            binding.userDetail.visibility = View.GONE
        }
    }

    override fun onSliderClick(item: SlideModel) {
        goToSliderActivity()
    }

}