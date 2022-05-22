package com.lifegate.app.ui.fragments.restaurant

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.databinding.RestaurantFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RestaurantFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = RestaurantFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: RestaurantViewModel
    private lateinit var navController: NavController
    private lateinit var binding: RestaurantFragmentBinding
    private val factory: RestaurantViewModelFactory by instance()

    private lateinit var adapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(RestaurantViewModel::class.java)
        binding = RestaurantFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        adapter = RestaurantAdapter {
            println(it.restaurant_name)
        }

        viewModel.fetchRestaurant()

        binding.restaurantRecyclerView.adapter = adapter

        initView()
    }

    private fun initView() {

        viewModel.liveRestaurantList.observe(viewLifecycleOwner, Observer { item ->
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