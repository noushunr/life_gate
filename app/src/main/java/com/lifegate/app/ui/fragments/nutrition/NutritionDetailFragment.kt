package com.lifegate.app.ui.fragments.nutrition

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
import com.lifegate.app.databinding.NutritionDetailFragmentBinding
import com.lifegate.app.ui.fragments.diet.DietPlanAdapter
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanFragmentDirections
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class NutritionDetailFragment : Fragment(), KodeinAware, NetworkListener,
    DietPlanAdapter.DietPlanClickListener {

    companion object {
        fun newInstance() = NutritionDetailFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: NutritionDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: NutritionDetailFragmentBinding
    private val factory: NutritionDetailViewModelFactory by instance()

    private lateinit var adapter: DietPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(NutritionDetailViewModel::class.java)
        binding = NutritionDetailFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: NutritionDetailFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId
        viewModel.date = safeArgs.date

        if (viewModel.isFirst) {
            viewModel.fetchMyDietPlan()
            viewModel.isFirst = false
        }

        adapter = DietPlanAdapter(this)

        binding.nutritionRecyclerView.adapter = adapter

        initView()
    }

    private fun initView() {

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myPlanList)
            if (item != null && item.size > 0) {
                hideProgress()
            }

            binding.caloriesTxt.text = viewModel.topCal
            binding.vitaminsTxt.text = viewModel.topVit
            binding.mineralTxt.text = viewModel.topMin
        })

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
        //
    }

    override fun onIncContentClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood, type: String, value: String?) {
        //
    }

    override fun onMainImageClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood) {

        var imageList = mutableListOf<SlideModel>()
        if (item.setfood_video!=null && item.setfood_video?.isNotEmpty()!!){
            if ( item.setfood_video?.contains(",")!!){
                var result: List<String>? = item.setfood_video?.split(",")?.map { it.trim() }

                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel( PRO_IMG_BASE_URL + i,""))
                    }
                }

            }else{
                imageList?.add(SlideModel( PRO_IMG_BASE_URL + item.setfood_video,""))
            }
            val action = NutritionDetailFragmentDirections.actionNutritionDetailFragmentToSliderActivity(
                Gson().toJson(imageList)
            )
            navController.safeNavigate(action)
        }
        
    }

    override fun onSubImageClick(item: MyNutritionPlanApi.MyNutritionPlanSetFood) {
        var imageList = mutableListOf<SlideModel>()
        if (item.setfood_image!=null && item.setfood_image?.isNotEmpty()!!){
            if ( item.setfood_image?.contains(",")!!){
                var result: List<String>? = item.setfood_image?.split(",")?.map { it.trim() }

                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel( PRO_IMG_BASE_URL + i,""))
                    }
                }

            }else{
                imageList?.add(SlideModel( PRO_IMG_BASE_URL + item.setfood_image,""))
            }
            val action = NutritionDetailFragmentDirections.actionNutritionDetailFragmentToSliderActivity(
                Gson().toJson(imageList)
            )
            navController.safeNavigate(action)
        }

    }

}