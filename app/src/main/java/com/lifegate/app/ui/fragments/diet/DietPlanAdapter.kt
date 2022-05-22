package com.lifegate.app.ui.fragments.diet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.MyNutritionPlanApi.MyNutritionPlanSetFood
import com.lifegate.app.databinding.ListNutritionBinding
import com.lifegate.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by Adithya T Raj.
 **/

class DietPlanAdapter(
    val clickListener: DietPlanClickListener
): RecyclerView.Adapter<DietPlanAdapter.ViewHolder>() {

    private var viewPosition: Int? = null

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<MyNutritionPlanSetFood>() {
        override fun areItemsTheSame(
            oldItem: MyNutritionPlanSetFood,
            newItem: MyNutritionPlanSetFood
        ): Boolean {
            return oldItem.setfood_id == newItem.setfood_id
        }

        override fun areContentsTheSame(
            oldItem: MyNutritionPlanSetFood,
            newItem: MyNutritionPlanSetFood
        ): Boolean {
            return oldItem.setfood_name == newItem.setfood_name
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<MyNutritionPlanSetFood>) {
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
        holder.binding.listNutritionHeaderTxt.text = item.setfood_name
        holder.binding.listNutritionDescTxt.text = item.setfood_name
        holder.binding.listNutritionCaloriesTxt.text = item.setfood_calories
        holder.binding.listNutritionCarbsTxt.text = item.setfood_carbs
        holder.binding.listNutritionProteinTxt.text = item.setfood_protien
        holder.binding.listNutritionFatTxt.text = item.setfood_fat
        if (item?.setfood_image!=null && item?.setfood_image?.isNotEmpty()!!){
            if (item?.setfood_image?.contains(",")!!){
                var result: List<String>? = item?.setfood_image?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.listNutritionSubImg, PRO_IMG_BASE_URL + result[0])
                }

            }else{
                loadImage(holder.binding.listNutritionSubImg, PRO_IMG_BASE_URL + item.setfood_image)
            }
        }else{
            loadImage(holder.binding.listNutritionSubImg, PRO_IMG_BASE_URL + item.setfood_image)
        }
        if (item?.foodVideoThumb!=null && item?.foodVideoThumb?.isNotEmpty()!!){
            if (item?.foodVideoThumb?.contains(",")!!){
                var result: List<String>? = item?.foodVideoThumb?.split(",")?.map { it.trim() }
                if (result?.size!! >0){
                    loadImage(holder.binding.listNutritionMainImg, PRO_IMG_BASE_URL + result[0])
                }

            }else{
                loadImage(holder.binding.listNutritionMainImg, PRO_IMG_BASE_URL + item.foodVideoThumb)
            }
        }else{
            loadImage(holder.binding.listNutritionMainImg, PRO_IMG_BASE_URL + item.setfood_video)
        }
//        try {
//            val mainImgList = item.setfood_image?.split(",")
//            if (mainImgList != null && mainImgList.size > 1) {
//                loadImage(holder.binding.listNutritionMainImg, PRO_IMG_BASE_URL + mainImgList[0])
//            }
//            val subImgList = item.setfood_prepare_images?.split(",")
//            if (subImgList != null && subImgList.size > 1) {
//                loadImage(holder.binding.listNutritionSubImg, PRO_IMG_BASE_URL + subImgList[0])
//            }
//        } catch (e: Exception) {
//
//        }

        holder.binding.listNutritionVitaminsTxt.text = item.setfood_vitamins
        holder.binding.listNutritionMineralsTxt.text = item.setfood_minarals

        holder.binding.listNutritionLayoutDown.setOnClickListener {
            viewPosition = position
            notifyDataSetChanged()
        }

        holder.binding.listNutritionLayoutUp.setOnClickListener {
            viewPosition = null
            notifyDataSetChanged()
        }

        when (viewPosition) {
            position -> {
                holder.binding.listNutritionLayoutHiddenContent.visibility = View.VISIBLE
                holder.binding.listNutritionLayoutDown.visibility = View.GONE
            }
            else -> {
                holder.binding.listNutritionLayoutHiddenContent.visibility = View.GONE
                holder.binding.listNutritionLayoutDown.visibility = View.VISIBLE
            }
        }

        holder.binding.calories.setOnClickListener {
            clickListener.onIncContentClick(item, KEY_TXT_CALORIES, item.setfood_calories)
        }

        holder.binding.listNutritionMainImg.setOnClickListener {
            clickListener.onMainImageClick(item)
        }
        holder.binding.listNutritionSubImg.setOnClickListener {
            clickListener.onSubImageClick(item)
        }

        holder.binding.carbs.setOnClickListener {
            clickListener.onIncContentClick(item, KEY_TXT_CARBS, item.setfood_carbs)
        }

        holder.binding.proteins.setOnClickListener {
            clickListener.onIncContentClick(item, KEY_TXT_PROTEINS, item.setfood_protien)
        }

        holder.binding.fat.setOnClickListener {
            clickListener.onIncContentClick(item, KEY_TXT_FAT, item.setfood_fat)
        }

        holder.binding.statusCheck.setOnClickListener {
            clickListener.onStatusClick(item)
        }

        if (item.userlog.isNullOrEmpty()) {
            holder.binding.tickImg.visibility = View.GONE
        } else {
            holder.binding.tickImg.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListNutritionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListNutritionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface DietPlanClickListener {
        fun onStatusClick(item: MyNutritionPlanSetFood)
        fun onIncContentClick(item: MyNutritionPlanSetFood, type: String, value: String?)
        fun onMainImageClick(item: MyNutritionPlanSetFood)
        fun onSubImageClick(item: MyNutritionPlanSetFood)
    }

}