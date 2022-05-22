package com.lifegate.app.ui.fragments.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lifegate.app.R
import com.lifegate.app.utils.*


/*
 *Created by Adithya T Raj on 30-04-2020
*/

class OnBoardAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = OnBoardViewFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_ON_BOARD_IMG, imageArray[position])
            putString(ARG_ON_BOARD_TITLE_1, titleArray1[position])
//            putString(ARG_ON_BOARD_TITLE_2, titleArray2[position])
//            putString(ARG_ON_BOARD_TITLE_3, titleArray3[position])
            putInt(ARG_ON_BOARD_DESC, position + 1)
        }
        return fragment
    }

    companion object {

        val imageArray =
            mutableListOf(
                R.drawable.ic_onboard_1,
                R.drawable.ic_onboard_2,
                R.drawable.ic_onboard_3,
                R.drawable.ic_onboard_4
            )

        val titleArray1 =
            mutableListOf(
                "Find The Best Fitness Coaches To Help you reach your Health and Fitness goal in Life & Gate App",
                "Find Sport Coaches To Help you Enhance your Skills and Level up in Life & Gate App",
                "Get a Nutrition Plan and How to Prepare your Healthy Meals from Nutritionist in Life & Gate App",
                "Calculate Your Intake Calories & Burned Calories In Life & Gate App"
            )

        val titleArray2 =
            mutableListOf(
                "Help you reach your Health and Fitness goal in",
                "Help you Enhance your Skills and Level up in",
                "Your Healthy Meals from Nutritionist in",
                "Burned Calories In"
            )

        val titleArray3 =
            mutableListOf(
                "Life & Gate App",
                "Life & Gate App",
                "Life & Gate App",
                "Life & Gate App"
            )
    }

}