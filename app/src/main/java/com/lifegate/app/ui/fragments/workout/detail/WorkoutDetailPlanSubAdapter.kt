package com.lifegate.app.ui.fragments.workout.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi.*
import com.lifegate.app.databinding.ListWorkoutHistoryBinding
import com.lifegate.app.ui.fragments.workout.plan.WorkoutPlanSubAdapter
import com.lifegate.app.utils.PRO_IMG_BASE_URL
import com.lifegate.app.utils.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class WorkoutDetailPlanSubAdapter(val clickListener: WorkoutClickListener) : RecyclerView.Adapter<WorkoutDetailPlanSubAdapter.ViewHolder>() {

    private var viewPosition: Int? = null

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<MyWorkoutPlanSubTitle>() {
        override fun areItemsTheSame(
            oldItem: MyWorkoutPlanSubTitle,
            newItem: MyWorkoutPlanSubTitle
        ): Boolean {
            return oldItem.workoutsublitle_id == newItem.workoutsublitle_id
        }

        override fun areContentsTheSame(
            oldItem: MyWorkoutPlanSubTitle,
            newItem: MyWorkoutPlanSubTitle
        ): Boolean {
            return oldItem.workoutsublitle_title == newItem.workoutsublitle_title
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<MyWorkoutPlanSubTitle>) {
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
        holder.binding.listWorkoutSubCountTxt.text = (position + 1).toString()
        holder.binding.listWorkoutSubTitleTxt.text = item.workoutsublitle_title
        holder.binding.listWorkoutSubRepTxt.text = item.workoutsublitle_rips
        holder.binding.listWorkoutSubSetsTxt.text = item.workoutsublitle_sets
        holder.binding.listWorkoutSubCaloriesTxt.text = item.workoutsublitle_caloriesburn
        holder.binding.listWorkoutSubRestTimeMinTxt.text = item.workoutsublitle_resttime + " min"
        holder.binding.listWorkoutSubWeightRecommendedTxt.text = item.workoutsublitle_use_weight
        holder.binding.listWorkoutSubNotesTxt.text = item.workoutsublitle_note
        if (item.workoutsublitle_pics!=null ){
            if (item.workoutsublitle_pics?.isNotEmpty()!! && item.workoutsublitle_pics?.contains(",")!!){
                var result: List<String>? = item.workoutsublitle_pics?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.listWorkoutSubImg, PRO_IMG_BASE_URL + result[0])
                }

            }else{
                loadImage(holder.binding.listWorkoutSubImg, PRO_IMG_BASE_URL + item.workoutsublitle_pics)
            }
        }else{
            loadImage(holder.binding.listWorkoutSubImg, PRO_IMG_BASE_URL + item.workoutsublitle_pics)
        }
//        loadImage(holder.binding.listWorkoutSubImg, PRO_IMG_BASE_URL + item.workoutsublitle_pics)

        holder.binding.listWorkoutSubImg.setOnClickListener{
            clickListener?.onImageClick(item)
        }
        holder.binding.listWorkoutSubLayoutDown.setOnClickListener {
            viewPosition = position
            notifyDataSetChanged()
        }

        holder.binding.listWorkoutSubLayoutUp.setOnClickListener {
            viewPosition = null
            notifyDataSetChanged()
        }

        when (viewPosition) {
            position -> {
                holder.binding.listWorkoutSubLayoutHiddenContent.visibility = View.VISIBLE
                holder.binding.listWorkoutSubLayoutDown.visibility = View.GONE
            }
            else -> {
                holder.binding.listWorkoutSubLayoutHiddenContent.visibility = View.GONE
                holder.binding.listWorkoutSubLayoutDown.visibility = View.VISIBLE
            }
        }

        if (item.userlog.isNullOrEmpty()) {
            holder.binding.tickImg.visibility = View.GONE
        } else {
            holder.binding.tickImg.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListWorkoutHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListWorkoutHistoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
    interface WorkoutClickListener {
        fun onImageClick(item: MyWorkoutPlanSubTitle)
    }

}