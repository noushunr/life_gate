package com.lifegate.app.ui.fragments.auth.signup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.R
import com.lifegate.app.databinding.SignUpFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class SignUpFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: SignUpViewModel
    private lateinit var navController: NavController
    private lateinit var binding: SignUpFragmentBinding
    private val factory: SignUpViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(SignUpViewModel::class.java)
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding?.lifecycleOwner = this

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.item.setOnClickListener {
            goToNextFrag()
        }

        binding.item1.setOnClickListener {
            navController.safePopBackStack()
        }

        viewModel.fetchCountry()

        initView()
    }

    private fun initView() {

        val modelListMap = mutableMapOf<String, String>()
        viewModel.liveCountry.observe(viewLifecycleOwner, Observer { itemList ->
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    modelListMap[data.name.toString()] = data.id.toString()
                }
            }
            val stateListAdapter = ArrayAdapter(
                requireContext(),
                R.layout.list_item_autocomplete,
                modelList
            )
            binding.countrySpinner.setAdapter(stateListAdapter)
            if (modelList.isNotEmpty()) {
                binding.countryInputLayout.isEnabled = true
            } else {
                binding.countryInputLayout.isEnabled = false
                binding.countrySpinner.clearFocus()
            }
            binding.countryInputLayout.clearFocus()
            binding.countrySpinner.text = null
            viewModel.countryName = null
        })
        binding.countrySpinner.setOnItemClickListener { parent, _, position, _ ->
            viewModel.countryName = parent.getItemAtPosition(position).toString()
            modelListMap[viewModel.countryName.toString()]?.let { modelId ->
                viewModel.countryId = modelId
            }
            viewModel.fetchCity(viewModel.countryId.toString())
            binding.countryInputLayout.clearFocus()
            Timber.e(viewModel.countryId.toString())
        }

        val cityListMap = mutableMapOf<String, String>()
        viewModel.liveCity.observe(viewLifecycleOwner, Observer { itemList ->
            viewModel.cityId = null
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    cityListMap[data.name.toString()] = data.id.toString()
                }
            }
            val stateListAdapter = ArrayAdapter(
                requireContext(),
                R.layout.list_item_autocomplete,
                modelList
            )
            binding.citySpinner.setAdapter(stateListAdapter)
            if (modelList.isNotEmpty()) {
                binding.cityInputLayout.isEnabled = true
            } else {
                binding.cityInputLayout.isEnabled = false
                binding.citySpinner.clearFocus()
            }
            binding.cityInputLayout.clearFocus()
            binding.citySpinner.text = null
            viewModel.cityId = null
        })
        binding.citySpinner.setOnItemClickListener { parent, _, position, _ ->
            viewModel.cityName = parent.getItemAtPosition(position).toString()
            cityListMap[viewModel.cityName.toString()]?.let { modelId ->
                viewModel.cityId = modelId
            }
            binding.cityInputLayout.clearFocus()
            Timber.e(viewModel.cityId.toString())
        }
    }

    private fun goToNextFrag() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToMyPlanFragment()
        navController.safeNavigate(action)
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        if (viewModel.isSignUp) {
            view?.context?.toast(viewModel.errorMessage)
            navController.safePopBackStack()
        }
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