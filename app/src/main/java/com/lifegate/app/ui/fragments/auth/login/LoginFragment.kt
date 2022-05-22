package com.lifegate.app.ui.fragments.auth.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.messaging.FirebaseMessaging
import com.lifegate.app.databinding.LoginFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class LoginFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: LoginViewModel
    private lateinit var navController: NavController
    private lateinit var binding: LoginFragmentBinding
    private val factory: LoginViewModelFactory by instance()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var deviceToken:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetForgotLayout)
        getDeviceToken()
        binding.loginGoToSignUp.setOnClickListener {
            goToNextFrag()
        }
        binding.tvForgotPassword?.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        viewModel?.mutableForgotPassword?.observe(viewLifecycleOwner,{
            hideProgress()
            view?.context?.toast(it?.message!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        })

    }

    private fun goToNextFrag() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
        navController.safeNavigate(action)
    }

    private fun goToMyPlanFrag() {
        val action = LoginFragmentDirections.actionLoginFragmentToMyPlanFragment()
        navController.safeNavigate(action)
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
        navController.safePopBackStack()
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

    fun getDeviceToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            deviceToken = task.result

            viewModel?.deviceToken = deviceToken

        })
    }
}