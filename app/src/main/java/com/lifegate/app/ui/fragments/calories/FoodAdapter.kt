package com.lifegate.app.ui.fragments.calories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.FoodMealListApi.FoodMealListData
import com.lifegate.app.databinding.ListFoodMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by Adithya T Raj.
 **/

class FoodAdapter(
    val clickListener: FoodClickListener
): RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<FoodMealListData>() {
        override fun areItemsTheSame(
            oldItem: FoodMealListData,
            newItem: FoodMealListData
        ): Boolean {
            return oldItem.groceries_id == newItem.groceries_id
        }

        override fun areContentsTheSame(
            oldItem: FoodMealListData,
            newItem: FoodMealListData
        ): Boolean {
            return oldItem.isSelected == newItem.isSelected
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<FoodMealListData>) {
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
        holder.binding.nameTxt.text = item.groceries_name
        holder.binding.calories.text = item.groceries_calory + "g"
        holder.binding.carbs.text = item.groceries_carb + "g"
        holder.binding.proteins.text = item.groceries_protien + "g"
        holder.binding.fats.text = item.groceries_fat + "g"

        /*holder.binding.valueTxt.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank() || text.isNullOrEmpty()) {
                clickListener.onUpdateQty(item, "0")
                notifyDataSetChanged()
            } else {
                clickListener.onUpdateQty(item, text.toString())
                notifyDataSetChanged()
            }
            Timber.e(text.toString())
        }*/

        holder.binding.mainLayout.setOnClickListener(null)

        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                clickListener.onUnSelectFoodClick(item)
            }else{
                clickListener.onSelectFoodClick(item)
            }
            notifyDataSetChanged()
        }
        holder.binding.mainLayout.setOnClickListener {
            holder.binding.checkbox.isChecked = !item.isSelected
//            clickListener.onUnSelectFoodClick(item)
//            notifyDataSetChanged()
        }
        if (item.isSelected) {
            holder.binding.tickImg.visibility = View.GONE
            holder.binding.checkbox.isChecked = true
        } else {
            holder.binding.tickImg.visibility = View.GONE
            holder.binding.checkbox.isChecked = false
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListFoodMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListFoodMenuBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface FoodClickListener {
        fun onSelectFoodClick(item:FoodMealListData)
        fun onUnSelectFoodClick(item:FoodMealListData)
        fun onUpdateQty(item:FoodMealListData, qty: String)
    }

}