package com.lifegate.app.ui.fragments.workout.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi.MyWorkoutPlanMainTitle
import com.lifegate.app.databinding.ListWorkoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class WorkoutDetailPlanAdapter(val clickListener: WorkoutDetailPlanSubAdapter.WorkoutClickListener): RecyclerView.Adapter<WorkoutDetailPlanAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<MyWorkoutPlanMainTitle>() {
        override fun areItemsTheSame(
            oldItem: MyWorkoutPlanMainTitle,
            newItem: MyWorkoutPlanMainTitle
        ): Boolean {
            return oldItem.workoutmainlitle_id == newItem.workoutmainlitle_id
        }

        override fun areContentsTheSame(
            oldItem: MyWorkoutPlanMainTitle,
            newItem: MyWorkoutPlanMainTitle
        ): Boolean {
            return oldItem.workoutmainlitle_title == newItem.workoutmainlitle_title
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<MyWorkoutPlanMainTitle>) {
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
        holder.binding.listWorkoutMainTitleTxt.text = item.workoutmainlitle_title
        val adapter = WorkoutDetailPlanSubAdapter(clickListener)
        holder.binding.listWorkoutMainRecyclerView.adapter = adapter
        val list = item.mywplansubtitle
        if (!list.isNullOrEmpty()) {
            adapter.submitList(list)
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListWorkoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}