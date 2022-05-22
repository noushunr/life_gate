package com.lifegate.app.ui.fragments.restaurant.nutrition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.NutritionGroceryResApi.*
import com.lifegate.app.databinding.ListRestaurantBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class NutritionRestaurantAdapter(
    val clickListener: (NutritionRestaurant) -> Unit
): RecyclerView.Adapter<NutritionRestaurantAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<NutritionRestaurant>() {
        override fun areItemsTheSame(
            oldItem: NutritionRestaurant,
            newItem: NutritionRestaurant
        ): Boolean {
            return oldItem.restaurant_name == newItem.restaurant_name
        }

        override fun areContentsTheSame(
            oldItem: NutritionRestaurant,
            newItem: NutritionRestaurant
        ): Boolean {
            return oldItem.restaurant_desc == newItem.restaurant_desc
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<NutritionRestaurant>) {
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
        holder.binding.listResNameTxt.text = item.restaurant_name
        holder.binding.listResPlaceTxt.text = item.restaurant_desc
        loadImage(holder.binding.listResImg, PRO_IMG_BASE_URL + item.restaurant_pic)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListRestaurantBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}