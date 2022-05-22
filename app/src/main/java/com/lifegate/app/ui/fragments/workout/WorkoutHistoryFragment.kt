package com.lifegate.app.ui.fragments.workout

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
import com.lifegate.app.data.network.responses.HistoryApi
import com.lifegate.app.databinding.WorkoutHistoryFragmentBinding
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanFragmentDirections
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class WorkoutHistoryFragment : Fragment(), KodeinAware, NetworkListener,
    WorkoutHistoryAdapter.WorkoutHistoryClickListener {

    companion object {
        fun newInstance() = WorkoutHistoryFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: WorkoutHistoryViewModel
    private lateinit var navController: NavController
    private lateinit var binding: WorkoutHistoryFragmentBinding
    private val factory: WorkoutHistoryViewModelFactory by instance()

    private lateinit var adapter: WorkoutHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(WorkoutHistoryViewModel::class.java)
        binding = WorkoutHistoryFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs:WorkoutHistoryFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        adapter = WorkoutHistoryAdapter(this)

        binding.workoutHistoryRecyclerView.adapter = adapter

        viewModel.fetchWorkoutHistory()

        initView()
    }

    private fun initView() {

        viewModel.liveData.observe(viewLifecycleOwner, Observer { item ->
            val plan = item.plan_details
            val historyList = item.history
            if (plan != null && !historyList.isNullOrEmpty()) {
                adapter.submitList(historyList.reversed(), plan)
            } else {
                adapter.submitList(mutableListOf(), null)
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

    override fun onWorkoutClick(item: HistoryApi.PlanHistory?) {
        val action = WorkoutHistoryFragmentDirections.actionWorkoutHistoryFragmentToWorkoutDetailFragment(
            viewModel.planId, viewModel.purchaseId, item?.log_date.toString()
        )
        navController.safeNavigate(action)
    }

    override fun onNutritionClick(item: HistoryApi.PlanHistory?) {
        //
    }

    override fun onImageClick(item: HistoryApi.PlanDetail?) {
        var imageList = mutableListOf<SlideModel>()
        if (item?.plan_image!=null && item.plan_image?.isNotEmpty()!!){
            if ( item.plan_image?.contains(",")!!){
                var result: List<String>? = item.plan_image?.split(",")?.map { it.trim() }

                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel( PRO_IMG_BASE_URL + i,""))
                    }
                }

            }else{
                imageList?.add(SlideModel( PRO_IMG_BASE_URL + item.plan_image,""))
            }
        }
        val action = WorkoutHistoryFragmentDirections.actionWorkoutHistoryFragmentToSliderActivity(
            Gson().toJson(imageList)
        )
        navController.safeNavigate(action)
    }

}