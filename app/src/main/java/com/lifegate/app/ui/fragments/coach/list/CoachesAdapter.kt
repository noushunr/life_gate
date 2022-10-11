package com.lifegate.app.ui.fragments.coach.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CoachesListApi.Coaches
import com.lifegate.app.databinding.ListCoachesBinding
import com.lifegate.app.utils.loadCircleImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachesAdapter(
    val clickListener: (Coaches) -> Unit
): RecyclerView.Adapter<CoachesAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<Coaches>() {
        override fun areItemsTheSame(
            oldItem: Coaches,
            newItem: Coaches
        ): Boolean {
            return oldItem.coach_id == newItem.coach_id
        }

        override fun areContentsTheSame(
            oldItem: Coaches,
            newItem: Coaches
        ): Boolean {
            return oldItem.coach_photo == newItem.coach_photo
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<Coaches>) {
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
        holder.binding.listCoachesTypeTxt.text = item.coach_type_name
        val coachName = StringBuilder()
        if (item.coach_fname != null) {
            coachName.append(item.coach_fname).append(" ")
        }
        if (item.coach_lname != null) {
            coachName.append(item.coach_lname)
        }
        holder.binding.listCoachesNameTxt.text = coachName
        val planStart = StringBuilder()
        if (item.plan_starts != null) {
            planStart.append("$ ").append(item.plan_starts)
        } else {
            planStart.append("$ ").append("0.00")
        }
        holder.binding.listCoachesPlanStartPriceTxt.text = planStart
        loadCircleImage(holder.binding.listCoachesPhotoImg, item.coach_photo)
        holder.binding.listCoachesAboutTxt.text = item.coach_about
        holder.binding.listCoachesCityTxt.text = "${item.country_name},\n${item.city_name}"
        holder.binding.listCoachesClubTxt.text = item.coach_club_name

        holder.binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCoachesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCoachesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}