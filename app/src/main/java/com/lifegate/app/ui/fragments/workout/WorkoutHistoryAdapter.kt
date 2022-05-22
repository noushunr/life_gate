package com.lifegate.app.ui.fragments.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.HistoryApi.*
import com.lifegate.app.databinding.ListHistoryBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * Created by Adithya T Raj.
 **/

class WorkoutHistoryAdapter(
    val clickListener: WorkoutHistoryClickListener
): RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private var planDetails: PlanDetail? = null

    private val diffCallBack = object : DiffUtil.ItemCallback<PlanHistory>() {
        override fun areItemsTheSame(
            oldItem: PlanHistory,
            newItem: PlanHistory
        ): Boolean {
            return oldItem.log_date == newItem.log_date
        }

        override fun areContentsTheSame(
            oldItem: PlanHistory,
            newItem: PlanHistory
        ): Boolean {
            return oldItem.log_date == newItem.log_date
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<PlanHistory>, planDetail: PlanDetail?) {
        planDetails = planDetail
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
        holder.binding.headerNameTxt.text = planDetails?.plan_name
        if (item.burned != null) {
            holder.binding.burnedTxt.text = item.burned + " Cal"
            holder.binding.burnedLayout.visibility = View.VISIBLE
        } else {
            holder.binding.burnedLayout.visibility = View.GONE
        }
        if (item.consumed != null) {
            holder.binding.consumedTxt.text = item.consumed + " Cal"
            holder.binding.consumedLayout.visibility = View.VISIBLE
        } else {
            holder.binding.consumedLayout.visibility = View.GONE
        }
        if (planDetails?.plan_image!=null ){
            if (planDetails?.plan_image?.isNotEmpty()!! && planDetails?.plan_image?.contains(",")!!){
                var result: List<String>? = planDetails?.plan_image?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + result[0])
                }

            }else{
                loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + planDetails?.plan_image)
            }
        }else{
            loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + planDetails?.plan_image)
        }

        holder.binding.planImg?.setOnClickListener {
            clickListener.onImageClick(planDetails)
        }
        holder.binding.nutritionDetail.setOnClickListener {
            clickListener.onNutritionClick(item)
        }
        holder.binding.workoutDetail.setOnClickListener {
            clickListener.onWorkoutClick(item)
        }

        holder.binding.logDateTxt.text = item.log_date

        try {
            holder.binding.logDateTxt.text = setDayMonthDate(item.log_date.toString())
        } catch (e: Exception) {

        }

        when (planDetails?.plan_basic_type) {
            KEY_PLAN_TYPE_WORKOUT -> {
                holder.binding.nutritionDetail.visibility = View.GONE
                holder.binding.workoutDetail.visibility = View.VISIBLE
            }
            KEY_PLAN_TYPE_NUTRITION -> {
                holder.binding.nutritionDetail.visibility = View.VISIBLE
                holder.binding.workoutDetail.visibility = View.GONE
            }
            else -> {
                holder.binding.nutritionDetail.visibility = View.GONE
                holder.binding.workoutDetail.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListHistoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface WorkoutHistoryClickListener {
        fun onWorkoutClick(item: PlanHistory?)
        fun onNutritionClick(item: PlanHistory?)
        fun onImageClick(item: PlanDetail?)
    }

}