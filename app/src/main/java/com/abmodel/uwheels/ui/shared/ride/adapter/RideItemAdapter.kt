package com.abmodel.uwheels.ui.shared.ride.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.databinding.RideListItemBinding
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime

class RideItemAdapter(
	private val onItemClicked: (Ride) -> Unit
) : ListAdapter<Ride, RideItemAdapter.RideItemViewHolder>(DiffCallback) {

	class RideItemViewHolder(
		private var binding: RideListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(ride: Ride) {
			binding.apply {
				wheelsType.text = ride.wheelsType
				source.text = ride.source.mainText
				destination.text = ride.destination.mainText
				capacity.text = capacity.context.getString(
					R.string.ride_capacity,
					ride.currentCapacity
				)

				ride.date.apply {
					date.text =
						date.context.getString(
							R.string.ride_date,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
				ride.creationDate.apply {
					info.text =
						info.context.getString(
							R.string.ride_info,
							ride.host.name,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): RideItemViewHolder {
		return RideItemViewHolder(
			RideListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: RideItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<Ride>() {
			override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(
				oldItem: Ride,
				newItem: Ride
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}
