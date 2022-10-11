package com.lifegate.app.ui.fragments.myplan.detail

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifegate.app.data.network.responses.NotificationApi.NotificationData
import com.lifegate.app.databinding.ListNotificationBinding
import com.lifegate.app.databinding.ListRulesBinding
import com.lifegate.app.utils.clearHtmlTag
import com.lifegate.app.utils.setDayMonthDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Adithya T Raj.
 **/

class PlanRulesAdapter(
    val clickListener: (String) -> Unit
): RecyclerView.Adapter<PlanRulesAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<String>) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.listRules.text = Html.fromHtml(item, Html.FROM_HTML_MODE_LEGACY)
        }else{
            holder.binding.listRules.text = Html.fromHtml(item)
        }

    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(
        val binding: ListRulesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListRulesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}