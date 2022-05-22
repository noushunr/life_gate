package com.lifegate.app.ui.fragments.home

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.HomeFeaturedPlansApi.HomeFeaturedPlans
import com.lifegate.app.databinding.ListHomeFeaturedPlanBinding
import com.lifegate.app.utils.SliderAdapter
import com.lifegate.app.utils.clearHtmlTag
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Adithya T Raj.
 **/

class HomeFeaturedPlanAdapter(
    val clickListener: (HomeFeaturedPlans) -> Unit
): RecyclerView.Adapter<HomeFeaturedPlanAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<HomeFeaturedPlans>() {
        override fun areItemsTheSame(
            oldItem: HomeFeaturedPlans,
            newItem: HomeFeaturedPlans
        ): Boolean {
            return oldItem.plan_id == newItem.plan_id
        }

        override fun areContentsTheSame(
            oldItem: HomeFeaturedPlans,
            newItem: HomeFeaturedPlans
        ): Boolean {
            return oldItem.plan_name == newItem.plan_name
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<HomeFeaturedPlans>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                differ.submitList(list.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.listHomeFeaturesPlanNameTxt.text = item.plan_name
        holder.binding.listHomeFeaturesPlanDescTxt.text = item.plan_desc?.clearHtmlTag()
        val coachName = StringBuilder()
        if (item.coachFname != null) {
            coachName.append(item.coachFname).append(" ")
        }
        if (item.coachLname != null) {
            coachName.append(item.coachLname)
        }
        holder.binding.listFeaturesPlanCoachName.text = "Coach: $coachName"
        val planStart = StringBuilder()
        if (item.plan_cost != null) {
            planStart.append("Plans start from: $").append(item.plan_cost)
        } else {
            planStart.append("Plans start from: $").append("0.00")
        }
        holder.binding.listHomeFeaturesPlanPriceTxt.text = planStart
        val slider = setSliders(item.plan_pics)
        val adapter = SliderAdapter(slider, holder.binding.listHomeFeaturesPlanSliderView.context)
        holder.binding.listHomeFeaturesPlanSliderView.also {
            it.stopAutoCycle()
            it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            it.setIndicatorAnimation(IndicatorAnimationType.WORM)
            it.indicatorSelectedColor = Color.WHITE
            it.indicatorUnselectedColor = Color.GRAY
            it.scrollTimeInSec = 3
            it.setSliderAdapter(adapter)
            it.isAutoCycle = true
            it.startAutoCycle()
            it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        }
        holder.binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListHomeFeaturedPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListHomeFeaturedPlanBinding.inflate(layoutInflater, parent, false)
                val displayMetrics = DisplayMetrics()
                var context = parent?.context
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//                val display = wm.defaultDisplay
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                var deviceWidth = displayMetrics.widthPixels
                var height = displayMetrics.heightPixels
                binding.constraint.updateLayoutParams<RecyclerView.LayoutParams> {

                    width = deviceWidth - 130
                }

                return ViewHolder(binding)
            }
        }

    }

    private fun setSliders(banner: MutableList<String>?): MutableList<SlideModel> {
        if (banner.isNullOrEmpty()) {
            return mutableListOf()
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            slideList.add(SlideModel(list))
        }
        return slideList
    }

}