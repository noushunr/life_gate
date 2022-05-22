package com.lifegate.app.ui.fragments.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lifegate.app.databinding.FragmentOnBoardViewBinding
import com.lifegate.app.utils.*


class OnBoardViewFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardViewBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        //viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf {
            it.containsKey(ARG_ON_BOARD_IMG)
            it.containsKey(ARG_ON_BOARD_TITLE_1)
//            it.containsKey(ARG_ON_BOARD_TITLE_2)
            it.containsKey(ARG_ON_BOARD_DESC)
        }?.apply {
            binding.onBoardViewImg.setImageResource(getInt(ARG_ON_BOARD_IMG))
            binding.onBoardViewTitle1.text = getString(ARG_ON_BOARD_TITLE_1)
//            binding.onBoardViewTitle2.text = getString(ARG_ON_BOARD_TITLE_2)
//            binding.onBoardViewTitle3.text = getString(ARG_ON_BOARD_TITLE_3)
            //on_board_view_desc.text = getInt(ARG_ON_BOARD_DESC).toString()
        }

    }
}