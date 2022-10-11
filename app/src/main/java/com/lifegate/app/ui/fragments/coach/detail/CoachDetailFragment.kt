package com.lifegate.app.ui.fragments.coach.detail

import android.graphics.Color
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
import com.lifegate.app.databinding.CoachDetailFragmentBinding
import com.lifegate.app.ui.fragments.home.HomeFragmentDirections
import com.lifegate.app.utils.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CoachDetailFragment : Fragment(), KodeinAware, NetworkListener, CoachDetailListener,SliderAdapter.SliderClickListener {

    companion object {
        fun newInstance() = CoachDetailFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: CoachDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: CoachDetailFragmentBinding
    private val factory: CoachDetailViewModelFactory by instance()

    private lateinit var serviceAdapter: CoachServiceAdapter
    private lateinit var awardsAdapter: CoachAwardsAdapter
    private lateinit var planAdapter: CoachPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(CoachDetailViewModel::class.java)
        binding = CoachDetailFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        //viewModel.coachListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val safeArgs: CoachDetailFragmentArgs by navArgs()
        viewModel.coachId = safeArgs.coachId

        viewModel.fetchCoachDetail()

        serviceAdapter = CoachServiceAdapter {
            println(it.service)
        }
        awardsAdapter = CoachAwardsAdapter {
            println(it.name)
        }
        planAdapter = CoachPlanAdapter {
            println(it.plan_id)
            goToNextFrag(it.plan_id.toString())
        }

        binding.coachDetailServiceRecyclerview.adapter = serviceAdapter
        binding.coachDetailCertificateRecyclerview.adapter = awardsAdapter
        binding.coachDetailPlanRecyclerview.adapter = planAdapter

        initView()
    }

    private fun initView() {
        viewModel.liveCoachData.observe(viewLifecycleOwner, Observer { item ->

            val adapter = SliderAdapter(viewModel.homeSlideList, requireContext(),this)
            binding.coachDetailSliderView.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.scrollTimeInSec = 3
                it.setSliderAdapter(adapter)
                it.isAutoCycle = false
                it.stopAutoCycle()
                it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            }

            binding.coachDetailNameTxt.text = viewModel.coachName
            binding.coachDetailTypeTxt.text = viewModel.coachType
            binding.coachDetailPlaceTxt.text =  "${item.country_name},\n${item.city_name}"
            binding.coachDetailClubTxt.text = viewModel.coachClub
            binding.coachDetailExpYrTxt.text = viewModel.coachExp
            binding.coachDetailRatingBar.rating = viewModel.coachRating
            binding.tvDescription.text = item.coach_about
            serviceAdapter.submitList(viewModel.coachServiceList)
            awardsAdapter.submitList(viewModel.coachAwardList)
            planAdapter.submitList(viewModel.coachPlanList)

        })

        binding.coachMessageBtn.setOnClickListener {
            binding.message.overlay.visibility = View.VISIBLE
        }

        binding.message.close.setOnClickListener {
            binding.message.overlay.visibility = View.GONE
        }

        binding.message.submitBtn.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                val valueTxt = binding.message.valueTxt.text
                if (valueTxt.isNullOrBlank() || valueTxt.isNullOrEmpty()) {
                    view?.context?.toast("Message cant be blank")
                } else {
                    viewModel.addCoachMessage(valueTxt.toString())
                    binding.message.valueTxt.setText(null)
                }
            } else {
                view?.context?.toast("Please login to continue")
            }
            binding.message.overlay.visibility = View.GONE
        }
    }

    private fun goToNextFrag(planId : String) {
        val action = CoachDetailFragmentDirections.actionCoachDetailFragmentToPlanDetailFragment(planId)
        navController.safeNavigate(action)
    }

    private fun goToSliderActivity() {
        val action = CoachDetailFragmentDirections.actionCoachDetailFragmentToSliderActivity(
            Gson().toJson(viewModel.homeSlideList)
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

    override fun onCoachDetail() {
        //
    }

    override fun onReviewList() {
        binding.coachDetailReviewTxt.text = "Reviews (" + viewModel.coachReviewsList.size + ")"
    }

    override fun onReviewAdd() {
        //
    }

    override fun onSliderClick(item: SlideModel) {
        goToSliderActivity()
    }

}