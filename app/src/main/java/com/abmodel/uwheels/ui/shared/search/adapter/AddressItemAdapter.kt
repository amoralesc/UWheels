package com.abmodel.uwheels.ui.shared.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.databinding.AddressListItemBinding
import com.abmodel.uwheels.ui.shared.search.CustomAddress

/**
 * [ListAdapter] to inflate the address list item and populate
 * address search results.
 */
class AddressItemAdapter(
	private val onItemClicked: (CustomAddress) -> Unit
) : ListAdapter<CustomAddress, AddressItemAdapter.AddressItemViewHolder>(DiffCallback) {

	class AddressItemViewHolder(
		private var binding: AddressListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(address: CustomAddress) {
			binding.apply {
				addressMainText.text = address.mainText
				addressSecondaryText.text = address.secondaryText
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): AddressItemViewHolder {
		return AddressItemViewHolder(
			AddressListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: AddressItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<CustomAddress>() {
			override fun areItemsTheSame(oldItem: CustomAddress, newItem: CustomAddress): Boolean {
				return oldItem.placeId == newItem.placeId
			}

			override fun areContentsTheSame(oldItem: CustomAddress, newItem: CustomAddress): Boolean {
				return oldItem == newItem
			}
		}
	}
}
