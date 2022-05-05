package com.abmodel.uwheels.ui.shared.search.adapter

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.databinding.AddressListItemBinding

/**
 * [ListAdapter] to inflate the address list item and populate
 * address search results.
 */
class AddressItemAdapter(
	private val onItemClicked: (Address) -> Unit
) : ListAdapter<Address, AddressItemAdapter.AddressItemViewHolder>(DiffCallback) {

	class AddressItemViewHolder(
		private var binding: AddressListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(address: Address) {
			binding.apply {
				addressName.text = address.featureName ?: "Error"
				addressLine.text = address.getAddressLine(0) ?: "Error"
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
		private val DiffCallback = object : DiffUtil.ItemCallback<Address>() {
			override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
				return oldItem.latitude == newItem.latitude &&
						oldItem.longitude == newItem.longitude
			}

			override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
				return oldItem.equals(newItem)
			}
		}
	}
}
