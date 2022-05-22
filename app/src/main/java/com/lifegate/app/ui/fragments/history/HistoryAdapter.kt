package com.lifegate.app.ui.fragments.history

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

/**
 * Created by Adithya T Raj.
 **/

class HistoryAdapter(
    val clickListener: HistoryClickListener
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<HistoryAllPlanData>() {
        override fun areItemsTheSame(
            oldItem: HistoryAllPlanData,
            newItem: HistoryAllPlanData
        ): Boolean {
            return oldItem.log_date == newItem.log_date
        }

        override fun areContentsTheSame(
            oldItem: HistoryAllPlanData,
            newItem: HistoryAllPlanData
        ): Boolean {
            return oldItem.log_date == newItem.log_date
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<HistoryAllPlanData>) {
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
        holder.binding.headerNameTxt.text = item?.plan_details?.plan_name
        if (item.burned != null) {
            holder.binding.burnedTxt.text = item.burned + " Cal"
            holder.binding.burnedLayout.visibility = View.VISIBLE
        } else {
            holder.binding.burnedTxt.text = "0 Cal"
            holder.binding.burnedLayout.visibility = View.VISIBLE
            //holder.binding.burnedLayout.visibility = View.GONE
        }
        if (item.consumed != null) {
            holder.binding.consumedTxt.text = item.consumed + " Cal"
            holder.binding.consumedLayout.visibility = View.VISIBLE
        } else {
            holder.binding.consumedTxt.text = "0 Cal"
            holder.binding.consumedLayout.visibility = View.VISIBLE
            //holder.binding.consumedLayout.visibility = View.GONE
        }

        if (item?.plan_details?.plan_image!=null ){
            if (item?.plan_details?.plan_image?.isNotEmpty()!! && item?.plan_details?.plan_image?.contains(",")!!){
                var result: List<String>? = item?.plan_details?.plan_image?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + result[0])
                }

            }else{
                loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + item.plan_details?.plan_image)
            }
        }else{
            loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + item?.plan_details?.plan_image)
        }
//        loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + item?.plan_details?.plan_image)
//        try {
//            val mainImgList = item?.plan_details?.plan_image?.split(",")
//            if (mainImgList != null && mainImgList.size > 1) {
//                loadImage(holder.binding.planImg, PRO_IMG_BASE_URL + mainImgList[0])
//            }
//        } catch (e: Exception) {
//
//        }

        holder.binding.nutritionDetail.setOnClickListener {
            clickListener.onNutritionClick(item)
        }
        holder.binding.workoutDetail.setOnClickListener {
            clickListener.onWorkoutClick(item)
        }

        when (item?.plan_details?.plan_basic_type) {
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

    interface HistoryClickListener {
        fun onWorkoutClick(item: HistoryAllPlanData?)
        fun onNutritionClick(item: HistoryAllPlanData?)
    }

}