package com.lifegate.app.ui.fragments.grocery

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
import com.lifegate.app.databinding.NutritionGroceryFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NutritionGroceryFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = NutritionGroceryFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: NutritionGroceryViewModel
    private lateinit var navController: NavController
    private lateinit var binding: NutritionGroceryFragmentBinding
    private val factory: NutritionGroceryViewModelFactory by instance()

    private lateinit var adapter: NutritionGroceryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(NutritionGroceryViewModel::class.java)
        binding = NutritionGroceryFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: NutritionGroceryFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        adapter = NutritionGroceryAdapter {
            println(it.realgrocery_name)
        }

        viewModel.fetchRestaurant()

        binding.groceryRecyclerView.adapter = adapter

        initView()
    }

    private fun initView() {

        viewModel.liveGroceryList.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(item)
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