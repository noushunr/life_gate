package com.lifegate.app.ui.fragments.coach.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.databinding.ListCountryPopupBinding
import com.lifegate.app.generated.callback.OnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class CoachesCountryAdapter(
    val clickListener: (CountriesApi.CountriesData) -> Unit
): RecyclerView.Adapter<CoachesCountryAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CountriesApi.CountriesData>() {

        override fun areItemsTheSame(
            oldItem: CountriesApi.CountriesData,
            newItem: CountriesApi.CountriesData
        ): Boolean {
            return oldItem.coachCountry == newItem.coachCountry
        }

        override fun areContentsTheSame(
            oldItem: CountriesApi.CountriesData,
            newItem: CountriesApi.CountriesData
        ): Boolean {
            return oldItem.name == newItem.name
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)
    private var lastCheckedPosition = -1

    fun submitList(list: List<CountriesApi.CountriesData>) {
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
        holder.binding.rbCountry.text = item.name
        holder?.binding.rbCountry.isChecked = position == lastCheckedPosition
        holder?.binding?.rbCountry?.setOnClickListener {
            val copyOfLastCheckedPosition: Int = lastCheckedPosition
            lastCheckedPosition = position
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastCheckedPosition)
            clickListener(item)
        }

    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListCountryPopupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListCountryPopupBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}