package com.lifegate.app.ui.fragments.message

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.databinding.CoachMessageFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CoachMessageFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = CoachMessageFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: CoachMessageViewModel
    private lateinit var navController: NavController
    private lateinit var binding: CoachMessageFragmentBinding
    private val factory: CoachMessageViewModelFactory by instance()

    private lateinit var adapter: CoachMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(CoachMessageViewModel::class.java)
        binding = CoachMessageFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        adapter = CoachMessageAdapter {
            println(it.message_id)
        }

        binding.notificationRecyclerview.adapter = adapter

        if (viewModel.getLoginStatus()) {
            viewModel.fetchMessages()
            binding.forceLoginTxt.visibility = View.GONE
        } else {
            binding.forceLoginTxt.visibility = View.VISIBLE
        }

        initView()
    }

    private fun initView() {

        viewModel.liveMessages.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myMessageList)
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