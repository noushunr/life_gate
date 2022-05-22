package com.lifegate.app.ui.fragments.workout.detail

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
import com.google.gson.Gson
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi
import com.lifegate.app.databinding.WorkoutDetailFragmentBinding
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanFragmentArgs
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanFragmentDirections
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class WorkoutDetailFragment : Fragment(), KodeinAware, NetworkListener,WorkoutDetailPlanSubAdapter.WorkoutClickListener {

    companion object {
        fun newInstance() = WorkoutDetailFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: WorkoutDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: WorkoutDetailFragmentBinding
    private val factory: WorkoutDetailViewModelFactory by instance()

    private lateinit var adapter: WorkoutDetailPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(WorkoutDetailViewModel::class.java)
        binding = WorkoutDetailFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: WorkoutPlanFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        adapter = WorkoutDetailPlanAdapter(this)

        binding.workoutDetailRecyclerView.adapter = adapter

        if (viewModel.isFirst) {
            viewModel.fetchMyWorkoutPlan()
            viewModel.isFirst = false
        }

        initView()
    }

    private fun initView() {

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myPlanList)
            if (item != null && item.size > 0) {
                hideProgress()
            }
        })

    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        //view?.context?.toast(viewModel.errorMessage)
        if (viewModel.totalTime.isNotEmpty()) {
            var timeInHr = viewModel.totalTime.toDouble()?.div(60)
            if (timeInHr!=0.0){
                val time = String.format("%.2f", timeInHr)
                binding.totalTimeTxt.text = "Total Time\n $time hr"
            }

        }
        binding.calBurnedTxt.text = "Calories\nBurned ${viewModel.caloriesBurned}cal"
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

    override fun onImageClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        var imageList = mutableListOf<SlideModel>()
        if (item.workoutsublitle_pics!=null && item.workoutsublitle_pics?.isNotEmpty()!!){
            if ( item.workoutsublitle_pics?.contains(",")!!){
                var result: List<String>? = item.workoutsublitle_pics?.split(",")?.map { it.trim() }

                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel(PRO_IMG_BASE_URL + i, ""))
                    }
                }

            }else{
                imageList?.add(SlideModel(PRO_IMG_BASE_URL + item.workoutsublitle_pics, ""))
            }
        }
        val action = WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToSliderActivity(
            Gson().toJson(imageList)
        )
        navController.safeNavigate(action)
    }

}