package com.lifegate.app.ui.fragments.myplan.detail

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
import com.lifegate.app.databinding.PlanDetailFragmentBinding
import com.lifegate.app.ui.activity.MainActivity
import com.lifegate.app.ui.fragments.coach.detail.CoachDetailFragmentDirections
import com.lifegate.app.utils.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PlanDetailFragment : Fragment(), KodeinAware, NetworkListener, PlanDetailListener,SliderAdapter.SliderClickListener {

    companion object {
        fun newInstance() = PlanDetailFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: PlanDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: PlanDetailFragmentBinding
    private val factory: PlanDetailViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(PlanDetailViewModel::class.java)
        binding = PlanDetailFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        viewModel.planListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val safeArgs: PlanDetailFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId

        viewModel.fetchPlanDetail()

        binding.planDetailPurchaseBtn.setOnClickListener {
            checkLoginStatus()
        }

        binding.planDetailMsgCoachTxt.setOnClickListener {
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

        initView()
    }

    private fun initView() {

        viewModel.livePlanData.observe(viewLifecycleOwner, Observer { item ->

            val adapter = SliderAdapter(viewModel.planSlideList, requireContext(),this)
            binding.planDetailSliderView.also {
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

            binding.planDetailNameTxt.text = viewModel.planName
            binding.planDetailPlanTypeTxt.text = viewModel.planType
            binding.planDetailDescTxt.text = viewModel.planDesc
            binding.planDetailRulesTxt.text = viewModel.planRule
            binding.planDetailDurationTxt.text = "${viewModel.planDuration} Days"

            binding.planDetailPriceTxt.text = viewModel.planPrice
            binding.planDetailExtraCostTxt.text = viewModel.planExtraCost
            binding.planDetailRatingBar.rating = viewModel.planRating

        })
    }

    private fun checkLoginStatus() {
        val status = viewModel.getLoginStatus()
        if (status) {
            goToNextFrag()
        } else {
            view?.context?.toast("Please login to proceed")
            (activity as MainActivity?)?.checkLoginStatus()
        }
    }

    private fun goToNextFrag() {
        val action = PlanDetailFragmentDirections.actionPlanDetailFragmentToCheckoutFragment(
            viewModel.planId, viewModel.planName, viewModel.planPic, viewModel.planPrice, viewModel.planExtraCost, viewModel.planDuration
        )
        navController.safeNavigate(action)
    }

    private fun goToSliderActivity() {
        val action = PlanDetailFragmentDirections.actionPlanDetailFragmentToSliderActivity(
            Gson().toJson(viewModel.planSlideList)
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

    override fun onPlanDetail() {
        //
    }

    override fun onReviewList() {
        binding.planDetailReviewTxt.text = "Reviews (" + viewModel.planReviewsList.size + ")"
    }

    override fun onReviewAdd() {
        //
    }

    override fun onSliderClick(item: SlideModel) {

        goToSliderActivity()
    }

}