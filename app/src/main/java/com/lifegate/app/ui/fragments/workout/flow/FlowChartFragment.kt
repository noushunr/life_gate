package com.lifegate.app.ui.fragments.workout.flow

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
import com.lifegate.app.databinding.FlowChartFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FlowChartFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = FlowChartFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: FlowChartViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FlowChartFragmentBinding
    private val factory: FlowChartViewModelFactory by instance()

    private lateinit var adapter: FlowChartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(FlowChartViewModel::class.java)
        binding = FlowChartFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: FlowChartFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        adapter = FlowChartAdapter {
            println(it.title)
        }

        binding.flowChartRecyclerview.adapter = adapter

        if (viewModel.getLoginStatus()) {
            viewModel.fetchFlowChart()
            binding.forceLoginTxt.visibility = View.GONE
        } else {
            binding.forceLoginTxt.visibility = View.VISIBLE
        }

        initView()
    }

    private fun initView() {

        viewModel.liveChart.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myChartList)
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