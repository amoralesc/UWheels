package com.abmodel.uwheels.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.RideUser
import com.abmodel.uwheels.databinding.RideUserListItemBinding
import com.bumptech.glide.Glide

class RideUserItemAdapter(
	private val onItemClicked: (RideUser) -> Unit
) : ListAdapter<RideUser, RideUserItemAdapter.RideUserItemViewHolder>(DiffCallback) {

	class RideUserItemViewHolder(
		private var binding: RideUserListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(rideUser: RideUser) {
			binding.apply {

				Glide
					.with(itemView.context)
					.load(rideUser.photoUrl)
					.placeholder(R.drawable.ic_account_circle)
					.error(R.drawable.ic_account_circle)
					.into(profilePhoto)

				name.text = name.context.getString(
					R.string.full_name, rideUser.name, rideUser.lastName
				)
				rating.text = rating.context.getString(
					R.string.rating, rideUser.rating.value
				)
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): RideUserItemViewHolder {
		return RideUserItemViewHolder(
			RideUserListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: RideUserItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<RideUser>() {
			override fun areItemsTheSame(oldItem: RideUser, newItem: RideUser): Boolean {
				return oldItem.uid == newItem.uid
			}

			override fun areContentsTheSame(
				oldItem: RideUser,
				newItem: RideUser
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}