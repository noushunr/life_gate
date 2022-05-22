package com.lifegate.app.ui.fragments.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.R
import com.lifegate.app.databinding.SettingsFragmentBinding
import com.lifegate.app.databinding.SplashFragmentBinding
import com.lifegate.app.ui.fragments.splash.SplashViewModelFactory
import com.lifegate.app.utils.safeNavigate
import com.lifegate.app.utils.safePopBackStack
import com.lifegate.app.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SettingsFragment : Fragment(), KodeinAware {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var navController: NavController
    private lateinit var binding: SettingsFragmentBinding
    private val factory: SettingsViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        //viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.coachRegistration.setOnClickListener {
            goToCoach()
        }

        binding.tvTerms.setOnClickListener {
            goToPolicy()
        }

        binding.logout.setOnClickListener {
            viewModel.logOutUser()
            requireContext().toast("Logging out the user")
            binding.logout.visibility = View.GONE
            if (navController?.previousBackStackEntry?.destination?.id == R.id.profileFragment){
                val action = SettingsFragmentDirections.actionSettingsFragmentToHomeFragment()
                navController.safeNavigate(action)
            }else{
                navController.safePopBackStack()
            }

        }
    }

    private fun goToCoach() {
        val action = SettingsFragmentDirections.actionGlobalCoachRegisterFragment()
        navController.safeNavigate(action)
    }

    private fun goToPolicy() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToPolicyFragment()
        navController.safeNavigate(action)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.getLoginStatus()) {
            binding.logout.visibility = View.VISIBLE
        } else {
            binding.logout.visibility = View.GONE
        }
    }

}