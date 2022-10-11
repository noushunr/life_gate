package com.lifegate.app.ui.fragments.splash

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.databinding.SplashFragmentBinding
import com.lifegate.app.utils.safeNavigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SplashFragment : Fragment(), KodeinAware {

    companion object {
        fun newInstance() = SplashFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: SplashViewModel
    private lateinit var navController: NavController
    private lateinit var binding: SplashFragmentBinding
    private val factory: SplashViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        //viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launch(Dispatchers.Main) {
            delay(5000)
            checkLocation()
        }
    }

    private fun checkLocation() {
        if (viewModel.getUserLatitude() == null) {
            goToHomeFrag()
//            goToNextFrag()
        } else {
            goToHomeFrag()
        }
    }

    private fun goToNextFrag() {
        val action = SplashFragmentDirections.actionSplashFragmentToChooseLocationFragment()
        navController.safeNavigate(action)
    }

    private fun goToHomeFrag() {
        val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
        navController.safeNavigate(action)
    }

}