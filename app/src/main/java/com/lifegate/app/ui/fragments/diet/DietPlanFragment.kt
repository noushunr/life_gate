package com.lifegate.app.ui.fragments.diet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.allanwang.kau.utils.toast
import com.google.gson.Gson
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.MyNutritionPlanApi
import com.lifegate.app.databinding.DietPlanFragmentBinding
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanFragmentDirections
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class DietPlanFragment : Fragment(), KodeinAware, NetworkListener,
    DietPlanAdapter.DietPlanClickListener {

    companion object {
        fun newInstance() = DietPlanFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: DietPlanViewModel
    private lateinit var navController: NavController
    private lateinit var binding: DietPlanFragmentBinding
    private val factory: DietPlanViewModelFactory by instance()

    private lateinit var adapter: DietPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(DietPlanViewModel::class.java)
        binding = DietPlanFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: DietPlanFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        if (viewModel.isFirst) {
            viewModel.fetchMyDietPlan()
            viewModel.isFirst = false
        }

        adapter = DietPlanAdapter(this)

        binding.dietPlanRecyclerView.adapter = adapter

        binding.restaurant.setOnClickListener {
            goToResFrag()
        }

        binding.grocery.setOnClickListener {
            goToGroceryFrag()
        }

        initView()
    }

    private fun initView() {

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myPlanList)
            if (item != null && item.size > 0) {
                hideProgress()
            }
            try {
                var cals = 0
                for (content in viewModel.myPlanList) {
                    if(content?.setfood_calories!=null && content?.setfood_calories?.isNotEmpty()!!) {
                        cals += content.setfood_calories.toString().toInt()
                    }
                }
                viewModel.topCal = "$cals"
            } catch (e: Exception) {
                Timber.e(e)
            }
            binding.dayTxt.text = "Day " + viewModel.topDay
            binding.caloriesTxt.text = viewModel.topCal
            binding.vitaminsTxt.text = viewModel.topVit
            binding.mineralTxt.text = viewModel.topMin
        })

        binding.log.close.setOnClickListener {
            binding.log.valueTxt.text = null
            binding.log.headerNameTxt.text = null
            binding.log.overlay.visibility = View.GONE
        }

    }

    private fun goToGroceryFrag() {
        val action = DietPlanFragmentDirections.actionDietPlanFragmentToNutritionGroceryFragment(
            viewModel.planId, viewModel.purchaseId
        )
        navController.safeNavigate(action)
    }

    private fun goToResFrag() {
        val action = DietPlanFragmentDirections.actionDietPlanFragmentToNutritionRestaurantFragment(
            viewModel.planId, viewModel.purchaseId
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

    override fun onStatusClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood) {
        viewModel.updateDietPlanStatus(item)
    }

    override fun onIncContentClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood, type: String, value: String?) {
        viewModel.calories = null
        viewModel.carbs = null
        viewModel.proteins = null
        viewModel.fat = null
        binding.log.valueTxt.setText(value)
        binding.log.headerNameTxt.text = type
        binding.log.submitBtn.setOnClickListener(null)
        binding.log.submitBtn.setOnClickListener{
            val valueInput = binding.log.valueTxt.text
            if (valueInput.isNullOrEmpty() || valueInput.isNullOrBlank()) {
                view?.toast("Please fill value")
            } else {
                when (type) {
                    KEY_TXT_CALORIES -> viewModel.calories = valueInput.toString()
                    KEY_TXT_CARBS -> viewModel.carbs = valueInput.toString()
                    KEY_TXT_PROTEINS -> viewModel.proteins = valueInput.toString()
                    KEY_TXT_FAT -> viewModel.fat = valueInput.toString()
                }
                viewModel.logDietPlan(item)
            }
            hideKeyboard()
        }
        binding.log.overlay.visibility = View.VISIBLE
    }

    override fun onMainImageClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood) {
        var imageList = mutableListOf<SlideModel>()
        if (item?.setfood_video!=null && item?.setfood_video?.isNotEmpty()!!){
            if (item?.setfood_video?.contains(",")!!){
                var result: List<String>? = item?.setfood_video?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel( PRO_IMG_BASE_URL + i,""))
                    }
                }

            }else{
                imageList?.add(SlideModel( PRO_IMG_BASE_URL + item.setfood_video,""))
            }
            val action = DietPlanFragmentDirections.actionDietPlanFragmentToSliderActivity(
                Gson().toJson(imageList)
            )
            navController.safeNavigate(action)
        }
    }

    override fun onSubImageClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood) {
        var imageList = mutableListOf<SlideModel>()
        if (item?.setfood_image!=null && item?.setfood_image?.isNotEmpty()!!){
            if (item?.setfood_image?.contains(",")!!){
                var result: List<String>? = item?.setfood_image?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel( PRO_IMG_BASE_URL + i,""))
                    }
                }

            }else{
                imageList?.add(SlideModel( PRO_IMG_BASE_URL + item.setfood_image,""))
            }
            val action = DietPlanFragmentDirections.actionDietPlanFragmentToSliderActivity(
                Gson().toJson(imageList)
            )
            navController.safeNavigate(action)
        }
    }

}