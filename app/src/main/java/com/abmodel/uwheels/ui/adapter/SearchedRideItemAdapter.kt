package com.abmodel.uwheels.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.SearchRideQuery
import com.abmodel.uwheels.data.model.WheelsType
import com.abmodel.uwheels.databinding.SearchedRideListItemBinding
import com.abmodel.uwheels.util.distanceTo
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime
import com.abmodel.uwheels.util.toLatLng

class SearchedRideItemAdapter(
	private val onItemClicked: (Ride) -> Unit,
	private val query: SearchRideQuery?
) : ListAdapter<Ride, SearchedRideItemAdapter.SearchedRideItemViewHolder>(DiffCallback) {

	class SearchedRideItemViewHolder(
		private var binding: SearchedRideListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(ride: Ride, query: SearchRideQuery?) {
			binding.apply {
				val sourceDistance =
					query?.let {
						ride.source.latLng!!.toLatLng().distanceTo(
							it.source.latLng!!.toLatLng()
						)
					}
				val destinationDistance =
					query?.let {
						ride.destination.latLng!!.toLatLng().distanceTo(
							it.destination.latLng!!.toLatLng()
						)
					}

				source.text = source.context.getString(
					R.string.address_with_distance,
					sourceDistance,
					ride.source.mainText
				)
				destination.text = destination.context.getString(
					R.string.address_with_distance,
					destinationDistance,
					ride.destination.mainText
				)

				rating.text = ride.rating.value.toString()
				capacity.text = capacity.context.getString(
					R.string.ride_capacity_short,
					ride.currentCapacity,
					ride.totalCapacity
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

				when (ride.wheelsType) {
					WheelsType.CLASSIC_WHEELS.toString() -> {
						additionalInfo.text =
							ride.price.toString()
					}
					else -> {
						additionalInfo.text =
							ride.transportation ?: ""
					}
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): SearchedRideItemViewHolder {
		return SearchedRideItemViewHolder(
			SearchedRideListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: SearchedRideItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current, query)
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
