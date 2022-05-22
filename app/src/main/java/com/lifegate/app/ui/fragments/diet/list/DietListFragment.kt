package com.lifegate.app.ui.fragments.diet.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.data.network.responses.MyPlanApi
import com.lifegate.app.databinding.DietListFragmentBinding
import com.lifegate.app.ui.fragments.myplan.MyPlanAdapter
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DietListFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = DietListFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: DietListViewModel
    private lateinit var navController: NavController
    private lateinit var binding: DietListFragmentBinding
    private val factory: DietListViewModelFactory by instance()

    private lateinit var planAdapter: MyPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(DietListViewModel::class.java)
        binding = DietListFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        planAdapter = MyPlanAdapter {
            println(it.plan_id)
            checkPlanType(it)
        }

        binding.myPlanRecyclerview.adapter = planAdapter

        if (viewModel.getLoginStatus()) {
            viewModel.fetchMyPlan()
            binding.forceLoginTxt.visibility = View.GONE
        } else {
            binding.forceLoginTxt.visibility = View.VISIBLE
        }

        initView()
    }

    private fun initView() {

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            planAdapter.submitList(viewModel.myPlanList)
            if (item != null && item.size > 0) {
                hideProgress()
            }else{
                binding.forceLoginTxt.visibility = View.VISIBLE
            }
        })

    }

    private fun checkPlanType(plan: MyPlanApi.MyPlanData) {
        //goToWorkout(plan)
        when (plan.plan_basic_type) {
            KEY_PLAN_TYPE_NUTRITION -> goToNutrition(plan)
            KEY_PLAN_TYPE_WORKOUT -> goToWorkout(plan)
        }
    }

    private fun goToNutrition(plan: MyPlanApi.MyPlanData) {
        val action = DietListFragmentDirections.actionDietListFragmentToDietPlanFragment(
            plan.plan_id.toString(), plan.purchase_id.toString()
        )
        navController.safeNavigate(action)
    }

    private fun goToWorkout(plan: MyPlanApi.MyPlanData) {
        val action = DietListFragmentDirections.actionDietListFragmentToWorkoutPlanFragment(
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

}