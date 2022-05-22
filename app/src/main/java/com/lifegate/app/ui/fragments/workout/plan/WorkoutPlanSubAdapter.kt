package com.lifegate.app.ui.fragments.workout.plan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.MyNutritionPlanApi
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi.*
import com.lifegate.app.databinding.ListWorkoutSubBinding
import com.lifegate.app.utils.PRO_IMG_BASE_URL
import com.lifegate.app.utils.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class WorkoutPlanSubAdapter(
    val clickListener: WorkoutClickListener
): RecyclerView.Adapter<WorkoutPlanSubAdapter.ViewHolder>() {

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
        holder.binding.listWorkoutSubRestTimeTxt.text = item.workoutsublitle_resttime_text
        holder.binding.listWorkoutSubRestTimeMinTxt.text = item.workoutsublitle_resttime + " min"
        holder.binding.listWorkoutSubWeightRecommendedTxt.text = item.workoutsublitle_use_weight
        holder.binding.listWorkoutSubTotalTimeTxt.text = "Total Time\n" + item.workoutsublitle_time + " min"
        holder.binding.listWorkoutSubNotesTxt.text = item.workoutsublitle_note
        holder.binding.listWorkoutSubCounterTxt.text = item.workoutsublitle_sets
        holder.binding.listWorkoutSubTimerTxt.text = "0:30"

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
        holder.binding.listWorkoutSubStatusUpdate.setOnClickListener {
            clickListener.onStatusClick(item)
        }

        holder.binding.listWorkoutSubUploadBtn.setOnClickListener {
            clickListener.onUploadClick(item)
        }

        holder.binding.listWorkoutSubAddWeightBtn.setOnClickListener {
            clickListener.onAddWeightClick(item)
        }

        holder.binding.listWorkoutSubPerformWorkoutBtn.setOnClickListener {
            clickListener.onPerformWorkoutClick(item)
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

        holder.binding.addNotesTxt.setOnClickListener {
            clickListener.onNotesClick(item)
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListWorkoutSubBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListWorkoutSubBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface WorkoutClickListener {
        fun onStatusClick(item: MyWorkoutPlanSubTitle)
        fun onUploadClick(item: MyWorkoutPlanSubTitle)
        fun onAddWeightClick(item: MyWorkoutPlanSubTitle)
        fun onPerformWorkoutClick(item: MyWorkoutPlanSubTitle)
        fun onNotesClick(item: MyWorkoutPlanSubTitle)
        fun onImageClick(item: MyWorkoutPlanSubTitle)
    }

}