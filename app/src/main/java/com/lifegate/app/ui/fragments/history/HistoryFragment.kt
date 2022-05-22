package com.lifegate.app.ui.fragments.history

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.data.network.responses.HistoryApi
import com.lifegate.app.databinding.HistoryFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HistoryFragment : Fragment(), KodeinAware, NetworkListener,
    HistoryAdapter.HistoryClickListener {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: HistoryViewModel
    private lateinit var navController: NavController
    private lateinit var binding: HistoryFragmentBinding
    private val factory: HistoryViewModelFactory by instance()

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(HistoryViewModel::class.java)
        binding = HistoryFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        adapter = HistoryAdapter(this)

        binding.historyMainRecyclerView.adapter = adapter

        if (viewModel.getLoginStatus()) {
            viewModel.fetchAllHistory()
            binding.forceLoginTxt.visibility = View.GONE
        } else {
            binding.forceLoginTxt.visibility = View.VISIBLE
        }

        initView()
    }

    private fun initView() {

        viewModel.liveHistory.observe(viewLifecycleOwner, Observer { item ->
            if (!item.isNullOrEmpty()) {
                adapter.submitList(item)
            } else {
                adapter.submitList(mutableListOf())
            }
        })

        /*viewModel.liveData.observe(viewLifecycleOwner, Observer { item ->
            val plan = item.plan_details
            val historyList = item.history
            if (plan != null && !historyList.isNullOrEmpty()) {
                adapter.submitList(historyList, plan)
            } else {
                adapter.submitList(mutableListOf(), null)
            }
        })*/
    }

    private fun goToNextFrag() {
        val action = HistoryFragmentDirections.actionHistoryFragmentToWorkoutHistoryFragment()
        navController.safeNavigate(action)
    }

    private fun goToNutritionFrag() {
        val action = HistoryFragmentDirections.actionHistoryFragmentToNutritionDetailFragment()
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

    override fun onWorkoutClick(item: HistoryApi.HistoryAllPlanData?) {
        viewModel.planId = item?.plan_details?.plan_id.toString()
        viewModel.purchaseId = item?.plan_details?.purchase_id.toString()
        viewModel.date = item?.log_date.toString()
        val action = HistoryFragmentDirections.actionHistoryFragmentToWorkoutDetailFragment(
            viewModel.planId, viewModel.purchaseId, viewModel.date
        )
        navController.safeNavigate(action)
    }

    override fun onNutritionClick(item: HistoryApi.HistoryAllPlanData?) {
        viewModel.planId = item?.plan_details?.plan_id.toString()
        viewModel.purchaseId = item?.plan_details?.purchase_id.toString()
        viewModel.date = item?.log_date.toString()
        val action = HistoryFragmentDirections.actionHistoryFragmentToNutritionDetailFragment(
            viewModel.planId, viewModel.purchaseId, viewModel.date
        )
        navController.safeNavigate(action)
    }

}