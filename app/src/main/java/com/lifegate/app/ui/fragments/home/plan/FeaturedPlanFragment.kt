package com.lifegate.app.ui.fragments.home.plan

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lifegate.app.data.network.responses.HomeFeaturedPlansApi
import com.lifegate.app.databinding.FeaturedPlanFragmentBinding
import com.lifegate.app.utils.fromJson
import com.lifegate.app.utils.safeNavigate

class FeaturedPlanFragment : Fragment() {

    companion object {
        fun newInstance() = FeaturedPlanFragment()
    }

    private lateinit var viewModel: FeaturedPlanViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FeaturedPlanFragmentBinding

    private lateinit var planAdapter: FeaturedPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FeaturedPlanViewModel::class.java)
        binding = FeaturedPlanFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        planAdapter = FeaturedPlanAdapter {
            println(it.plan_id)
            goToPlanDetail(it.plan_id.toString())
        }

        binding.featuredPlanRecyclerview.adapter = planAdapter

        val safeArgs: FeaturedPlanFragmentArgs by navArgs()
        val list : MutableList<HomeFeaturedPlansApi.HomeFeaturedPlans> = fromJson(safeArgs.json)

        planAdapter.submitList(list)
    }

    private fun goToPlanDetail(id: String) {
        val action = FeaturedPlanFragmentDirections.actionFeaturedPlanFragmentToPlanDetailFragment(
            id
        )
        navController.safeNavigate(action)
    }

}