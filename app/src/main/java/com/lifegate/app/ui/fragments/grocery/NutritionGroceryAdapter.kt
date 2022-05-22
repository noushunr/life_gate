package com.lifegate.app.ui.fragments.grocery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.NutritionGroceryResApi.*
import com.lifegate.app.databinding.ListGroceryBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class NutritionGroceryAdapter(
    val clickListener: (NutritionGrocery) -> Unit
): RecyclerView.Adapter<NutritionGroceryAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<NutritionGrocery>() {
        override fun areItemsTheSame(
            oldItem: NutritionGrocery,
            newItem: NutritionGrocery
        ): Boolean {
            return oldItem.realgrocery_id == newItem.realgrocery_id
        }

        override fun areContentsTheSame(
            oldItem: NutritionGrocery,
            newItem: NutritionGrocery
        ): Boolean {
            return oldItem.realgrocery_name == newItem.realgrocery_name
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<NutritionGrocery>) {
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
        holder.binding.listGroceryNameTxt.text = item.realgrocery_name
        holder.binding.listGroceryDescTxt.text = item.realgrocery_desc
        loadImage(holder.binding.listGroceryImg, PRO_IMG_BASE_URL + item.realgrocery_pic)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListGroceryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListGroceryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}