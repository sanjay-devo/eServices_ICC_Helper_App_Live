package com.icc.eserviceshelper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.icc.eserviceshelper.R
import com.icc.eserviceshelper.databinding.*
import com.icc.eserviceshelper.models.InfoItem

class InfoAdapter(
    private val items: List<InfoItem>,
    private val onAction: (InfoItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION = 0
        private const val TYPE_CONTACT = 1
        private const val TYPE_LINK = 2
        private const val TYPE_APP_INFO = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is InfoItem.Section -> TYPE_SECTION
            is InfoItem.Contact -> TYPE_CONTACT
            is InfoItem.Link -> TYPE_LINK
            is InfoItem.AppInfo -> TYPE_APP_INFO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION -> SectionViewHolder(ItemInfoSectionBinding.inflate(inflater, parent, false))
            TYPE_CONTACT -> ContactViewHolder(ItemInfoContactBinding.inflate(inflater, parent, false))
            TYPE_LINK -> LinkViewHolder(ItemInfoLinkBinding.inflate(inflater, parent, false))
            TYPE_APP_INFO -> AppInfoViewHolder(ItemInfoAppInfoBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is InfoItem.Section -> (holder as SectionViewHolder).bind(item)
            is InfoItem.Contact -> (holder as ContactViewHolder).bind(item)
            is InfoItem.Link -> (holder as LinkViewHolder).bind(item)
            is InfoItem.AppInfo -> (holder as AppInfoViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    inner class SectionViewHolder(private val binding: ItemInfoSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem.Section) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            if (item.icon != null) {
                binding.ivIcon.visibility = View.VISIBLE
                binding.ivIcon.setImageResource(item.icon)
            } else {
                binding.ivIcon.visibility = View.GONE
            }

            if (item.isWarning) {
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_theme_light_primaryContainer))
                binding.tvTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_theme_light_onPrimaryContainer))
                binding.tvContent.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_theme_light_onPrimaryContainer))
                binding.ivIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.md_theme_light_onPrimaryContainer))
            } else {
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.tvTitle.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_theme_light_primary))
                binding.tvContent.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.ivIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.md_theme_light_primary))
            }
        }
    }

    inner class ContactViewHolder(private val binding: ItemInfoContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem.Contact) {
            binding.tvLabel.text = item.title
            binding.tvValue.text = item.value
            binding.ivIcon.setImageResource(item.icon)
            binding.contactLayout.setOnClickListener { onAction(item) }
        }
    }

    inner class LinkViewHolder(private val binding: ItemInfoLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem.Link) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            item.icon?.let { binding.ivIcon.setImageResource(it) }
            binding.btnOpen.setOnClickListener { onAction(item) }
        }
    }

    inner class AppInfoViewHolder(private val binding: ItemInfoAppInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem.AppInfo) {
            binding.tvLabel.text = item.label
            binding.tvValue.text = item.value
            binding.ivIcon.setImageResource(item.icon)
        }
    }
}