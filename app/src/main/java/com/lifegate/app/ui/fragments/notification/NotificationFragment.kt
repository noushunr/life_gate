package com.lifegate.app.ui.fragments.notification

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lifegate.app.databinding.NotificationFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NotificationFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: NotificationViewModel
    private lateinit var navController: NavController
    private lateinit var binding: NotificationFragmentBinding
    private val factory: NotificationViewModelFactory by instance()

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(NotificationViewModel::class.java)
        binding = NotificationFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        adapter = NotificationAdapter {
            println(it.notification_title)
        }

        binding.notificationRecyclerview.adapter = adapter

        if (viewModel.getLoginStatus()) {
            viewModel.fetchNotification()
            binding.forceLoginTxt.visibility = View.GONE
        } else {
            binding.forceLoginTxt.visibility = View.VISIBLE
        }

        initView()
    }

    private fun initView() {

        viewModel.liveNotification.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myNotificationList)
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