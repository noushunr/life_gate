package com.lifegate.app.ui.fragments.myplan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.utils.visible
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.MyPlanApi.*
import com.lifegate.app.databinding.ListMyPlanBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class MyPlanAdapter(
    val clickListener: (MyPlanData) -> Unit
): RecyclerView.Adapter<MyPlanAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<MyPlanData>() {
        override fun areItemsTheSame(
            oldItem: MyPlanData,
            newItem: MyPlanData
        ): Boolean {
            return oldItem.plan_id == newItem.plan_id
        }

        override fun areContentsTheSame(
            oldItem: MyPlanData,
            newItem: MyPlanData
        ): Boolean {
            return oldItem.plan_name == newItem.plan_name
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<MyPlanData>) {
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
        holder.binding.listMyPlanNameTxt.text = item.plan_name
        if (item.plan_purchase_status!=null){
            holder.binding.listMyPlanTypeTxt.visibility = View.VISIBLE
            holder.binding.listMyPlanTypeTxt.text = "Status: ${item.plan_purchase_status}"
        }else{
            holder.binding.listMyPlanTypeTxt.visibility = View.GONE
        }

        holder.binding.listMyPlanDescTxt.text = item.plan_desc?.clearHtmlTag()
        holder.binding.listMyPlanStartDateTxt.text = setMonthDate(item.purchase_start_date.toString())
        holder.binding.listMyPlanEndDateTxt.text = setMonthDate(item.purchase_exp_date.toString())
        if (item?.plan_image!=null){
            if (item?.plan_image?.isNotEmpty()!! && item?.plan_image?.contains(",")!!){
                var result: List<String>? = item?.plan_image?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.listMyPlanImg, PRO_IMG_BASE_URL + result[0])
                }

            } else{
                loadImage(holder.binding.listMyPlanImg, PRO_IMG_BASE_URL + item.plan_image)
            }
        }else{
            loadImage(holder.binding.listMyPlanImg, PRO_IMG_BASE_URL + item.plan_image)
        }

        holder.binding.root.setOnClickListener {
            clickListener(item)
        }
        when (item.plan_basic_type) {
            KEY_PLAN_TYPE_NUTRITION -> {
                holder.binding.mainCardView.setCardBackgroundColor(Color.parseColor("#F8D008"))
            }
            else -> {
                holder.binding.mainCardView.setCardBackgroundColor(Color.parseColor("#37B7FE"))
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListMyPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListMyPlanBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    private fun setSliders(banner: MutableList<String>?): MutableList<SlideModel> {
        if (banner.isNullOrEmpty()) {
            return mutableListOf()
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            slideList.add(SlideModel(list))
        }
        return slideList
    }

}