package com.lifegate.app.ui.fragments.workout.plan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.EquipmentListApi.*
import com.lifegate.app.databinding.ListEquipmentsBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class EquipmentAdapter(
    val clickListener: (EquipmentData) -> Unit
): RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<EquipmentData>() {
        override fun areItemsTheSame(
            oldItem: EquipmentData,
            newItem: EquipmentData
        ): Boolean {
            return oldItem.equipments_id == newItem.equipments_id
        }

        override fun areContentsTheSame(
            oldItem: EquipmentData,
            newItem: EquipmentData
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<EquipmentData>) {
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
        holder.binding.titleTxt.text = item.equipments_name
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListEquipmentsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListEquipmentsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}