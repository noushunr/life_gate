package com.lifegate.app.ui.fragments.onboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.lifegate.app.databinding.OnBoardFragmentBinding
import com.lifegate.app.utils.safeNavigate
import com.google.android.material.tabs.TabLayoutMediator
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class OnBoardFragment : Fragment(), KodeinAware {

    companion object {
        fun newInstance() = OnBoardFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: OnBoardViewModel
    private lateinit var navController: NavController
    private lateinit var binding: OnBoardFragmentBinding
    private val factory: OnBoardViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(OnBoardViewModel::class.java)
        binding = OnBoardFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        //viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.onBoardPager.adapter = OnBoardAdapter(this)

        TabLayoutMediator(binding.onBoardTabIndicator, binding.onBoardPager) { _, _ ->
        }.attach()

        binding.onBoardPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position.equals(3)) {
                    binding.onBoardNextBtn.text = "Get Start"
                } else {
                    binding.onBoardNextBtn.text = "Next"
                }
            }

        })

        binding.onBoardSkipBtn.setOnClickListener {
            binding.onBoardPager.currentItem = 3
        }

        binding.onBoardNextBtn.setOnClickListener {
            when (binding.onBoardPager.currentItem) {
                3 -> {
                    goToNextFrag()
                }
                else -> binding.onBoardPager.currentItem = binding.onBoardPager.currentItem + 1
            }

        }


    }

    private fun goToNextFrag() {
        val action = OnBoardFragmentDirections.actionOnBoardFragmentToHomeFragment()
        navController.safeNavigate(action)
    }

}