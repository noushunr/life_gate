package com.lifegate.app.ui.fragments.pricy_policy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.R
import com.lifegate.app.databinding.PolicyFragmentBinding
import com.lifegate.app.databinding.SettingsFragmentBinding
import com.lifegate.app.ui.fragments.coach.list.CoachesViewModel
import com.lifegate.app.ui.fragments.coach.list.CoachesViewModelFactory
import com.lifegate.app.utils.NetworkListener
import com.lifegate.app.utils.hideProgress
import com.lifegate.app.utils.showProgress
import com.lifegate.app.utils.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PolicyFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = PolicyFragment()
    }
    override val kodein by kodein()
    private lateinit var navController: NavController
    private lateinit var viewModel: PolicyViewModel
    private lateinit var binding: PolicyFragmentBinding
    private val factory: PolicyViewModelFactory by instance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(PolicyViewModel::class.java)
        binding = PolicyFragmentBinding.inflate(inflater, container, false)
        viewModel.listener = this
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        initView()
        if (viewModel.isFirstTime) {
            viewModel.fetchPolicyData()
            viewModel.isFirstTime = !viewModel.isFirstTime
        }
    }

    private fun initView() {
        viewModel?.livePolicyData?.observe(viewLifecycleOwner) {
            binding?.tvPolicy.text = Html.fromHtml(it?.policyContent)
            binding?.tvPolicy.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onStarted() {
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
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