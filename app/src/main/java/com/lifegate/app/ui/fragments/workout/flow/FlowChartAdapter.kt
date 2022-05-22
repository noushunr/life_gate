package com.lifegate.app.ui.fragments.workout.flow

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.PlanFlowChartApi.*
import com.lifegate.app.databinding.ListFlowChartBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class FlowChartAdapter(
    val clickListener: (PlanFlowChart) -> Unit
): RecyclerView.Adapter<FlowChartAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<PlanFlowChart>() {
        override fun areItemsTheSame(
            oldItem: PlanFlowChart,
            newItem: PlanFlowChart
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: PlanFlowChart,
            newItem: PlanFlowChart
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<PlanFlowChart>) {
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
        holder.binding.listFlowTitleTxt.text = item.title
        when (item.status) {
            KEY_FLOW_OPEN -> {
                holder.binding.mainLayout.setCardBackgroundColor(Color.WHITE)
                loadDrawableImage(holder.binding.lock, R.drawable.ic_lock_open)
            }
            else -> {
                holder.binding.mainLayout.setCardBackgroundColor(
                    Color.argb(
                        100,
                        180,
                        180,
                        180
                    )
                )
                loadDrawableImage(holder.binding.lock, R.drawable.ic_lock)
                try {
                    val list = item.title?.split(":")
                    holder.binding.listFlowTitleTxt.text = (list?.get(0) ?: null) + " : $KEY_TXT_OFF"
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListFlowChartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListFlowChartBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}