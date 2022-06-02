package com.abmodel.uwheels.ui.shared.data

import android.app.Application
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.*
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.data.repository.chat.FirebaseChatRepository
import com.abmodel.uwheels.data.repository.notification.FirebaseNotificationRepository
import com.abmodel.uwheels.data.repository.ride.FirebaseRidesRepository
import com.abmodel.uwheels.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SharedViewModel(application: Application) : AndroidViewModel(application) {

	companion object {
		const val TAG = "SharedViewModel"
	}

	private val authRepository = FirebaseAuthRepository.getInstance()
	private val ridesRepository = FirebaseRidesRepository.getInstance()
	private val chatRepository = FirebaseChatRepository.getInstance()
	private val notificationRepository = FirebaseNotificationRepository.getInstance()

	// TODO: TOO MUCH BOILERPLATE CODE!!!

	private val _userRides = MutableLiveData<List<Ride>>(emptyList())

	private val activeUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status != RideStatus.COMPLETED.toString() &&
						ride.status != RideStatus.CANCELLED.toString() &&
						ride.requests.none { request ->
							request.user.uid == authRepository.getLoggedInUser().uid
						}
			}
			MutableLiveData(filteredRides)
		}

	private val hostedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.host.uid == authRepository.getLoggedInUser().uid &&
						ride.status != RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val requestedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.requests.any { request ->
					request.user.uid == authRepository.getLoggedInUser().uid
				}
			}
			MutableLiveData(filteredRides)
		}

	private val completedUserRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_userRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.status == RideStatus.COMPLETED.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val _userRidesFilter = MutableLiveData(UserRidesFilter.ACTIVE)
	val userRidesFilter: LiveData<UserRidesFilter>
		get() = _userRidesFilter

	val filteredUserRides: LiveData<List<Ride>> =
		Transformations.switchMap(_userRidesFilter) { filter ->
			when (filter) {
				UserRidesFilter.ACTIVE -> activeUserRides
				UserRidesFilter.REQUESTED -> requestedUserRides
				UserRidesFilter.COMPLETED -> completedUserRides
				UserRidesFilter.HOSTED -> hostedUserRides
				else -> null
			}
		}

	private val _selectedUserRideId: MutableLiveData<String> = MutableLiveData()
	private val selectedUserRideId: LiveData<String>
		get() = _selectedUserRideId

	val selectedUserRide: LiveData<Ride> =
		Transformations.switchMap(filteredUserRides) { rides ->
			Transformations.switchMap(_selectedUserRideId) { rideId ->
				MutableLiveData(rides.find { ride ->
					ride.id == rideId
				})
			}
		}

	private val _searchedRides = MutableLiveData<List<Ride>>(emptyList())

	private val classicWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.CLASSIC_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val sharedWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.SHARED_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val weWheelsSearchedRides: LiveData<List<Ride>>
		get() = Transformations.switchMap(_searchedRides) { rides ->
			val filteredRides = rides.filter { ride ->
				ride.wheelsType == WheelsType.WE_WHEELS.toString()
			}
			MutableLiveData(filteredRides)
		}

	private val _searchedRidesFilter = MutableLiveData(WheelsType.CLASSIC_WHEELS)
	val searchedRidesFilter: LiveData<WheelsType>
		get() = _searchedRidesFilter

	val filteredSearchedRides: LiveData<List<Ride>> =
		Transformations.switchMap(_searchedRidesFilter) { filter ->
			when (filter) {
				WheelsType.CLASSIC_WHEELS -> classicWheelsSearchedRides
				WheelsType.SHARED_WHEELS -> sharedWheelsSearchedRides
				WheelsType.WE_WHEELS -> weWheelsSearchedRides
				else -> null
			}
		}

	private val _selectedSearchedRideId: MutableLiveData<String?> = MutableLiveData()
	val selectedSearchedRideId: LiveData<String?>
		get() = _selectedSearchedRideId

	val selectedSearchedRide: MediatorLiveData<Ride?> =
		MediatorLiveData<Ride?>().apply {
			addSource(selectedSearchedRideId) {
				val ride = filteredSearchedRides.value?.find { ride ->
					ride.id == it
				}
				value = ride
			}
		}

	private val _requestResult = MutableLiveData<FormResult>()
	val requestResult: LiveData<FormResult>
		get() = _requestResult

	private val _chats = MutableLiveData<List<Chat>>(emptyList())
	val chats: LiveData<List<Chat>>
		get() = _chats

	private val _selectedChatId: MutableLiveData<String> = MutableLiveData()
	val selectedChatId: LiveData<String>
		get() = _selectedChatId

	val selectedChat: LiveData<Chat> =
		Transformations.switchMap(chats) { chats ->
			Transformations.switchMap(_selectedChatId) { chatId ->
				MutableLiveData(chats.find { chat ->
					chat.id == chatId
				})
			}
		}

	private val _messages = MutableLiveData<List<Message>>(emptyList())
	val messages: LiveData<List<Message>>
		get() = _messages

	private var fetchUserRidesJob: Job? = null
	private var fetchSearchedRidesJob: Job? = null
	private var fetchChatsJob: Job? = null
	private var fetchChatJob: Job? = null
	private var fetchNotificationsJob: Job? = null

	private var _query: SearchRideQuery? = null
	val query: SearchRideQuery?
		get() = _query

	init {
		Log.d(TAG, "SharedViewModel initialized")

		restartUserRidesUpdates()
		startChatsUpdates()
		startNotificationsUpdates()
		restartSearch()
	}

	fun setUserRidesFilter(filter: UserRidesFilter) {
		_userRidesFilter.postValue(filter)
	}

	fun selectUserRide(rideId: String) {
		_selectedUserRideId.postValue(rideId)
	}

	fun setSearchedRidesFilter(filter: WheelsType) {
		_searchedRidesFilter.postValue(filter)
	}

	fun selectSearchedRide(rideId: String) {
		_selectedSearchedRideId.value = rideId
	}

	fun selectChat(chatId: String) {
		_selectedChatId.value = chatId
	}

	fun acceptRideRequest(request: RideRequest) {
		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.acceptRideRequest(selectedUserRideId.value!!, request)
		}
	}

	fun rejectRideRequest(request: RideRequest) {
		viewModelScope.launch(Dispatchers.IO) {
			ridesRepository.rejectRideRequest(selectedUserRideId.value!!, request)
		}
	}

	fun requestRide() {
		viewModelScope.launch(Dispatchers.IO) {
			if (selectedSearchedRideId.value == null ||
				selectedSearchedRide.value == null ||
				query == null
			) {
				return@launch
			}

			_requestResult.postValue(
				FormResult(message = R.string.requesting_ride)
			)

			ridesRepository.requestRide(
				selectedSearchedRideId.value!!,
				RideRequest(
					user = RideUser.fromLoggedInUser(
						authRepository.getLoggedInUser()
					),
					sentDate = getCurrentDateAsCustomDate(),
					source = query!!.source,
					destination = query!!.destination,
					sourceDistance = query!!.source.latLng!!.toLatLng().distanceTo(
						selectedSearchedRide.value!!.source.latLng!!.toLatLng()
					),
					destinationDistance = query!!.destination.latLng!!.toLatLng().distanceTo(
						selectedSearchedRide.value!!.destination.latLng!!.toLatLng()
					),
					date = query!!.date,
					dateDifference = query!!.date.difference(
						selectedSearchedRide.value!!.date
					)
				)
			)

			_requestResult.postValue(
				FormResult(success = true, message = R.string.request_sent)
			)
			_selectedSearchedRideId.postValue(null)
			selectedSearchedRide.postValue(null)
		}
	}

	fun restartUserRidesUpdates(driverMode: Boolean? = null) {
		fetchUserRidesJob?.cancel()

		if (driverMode != null) {
			if (driverMode) {
				fetchUserRides(true, WheelsType.CLASSIC_WHEELS)
			} else {
				fetchUserRides()
			}
		} else {
			if (authRepository.isDriverModeOn()) {
				fetchUserRides(true, WheelsType.CLASSIC_WHEELS)
			} else {
				fetchUserRides()
			}
		}
	}

	fun stopUserRidesUpdates() {
		fetchUserRidesJob?.cancel()
	}

	private fun fetchUserRides(
		hostedOnly: Boolean = false,
		wheelsType: WheelsType? = null
	) {

		fetchUserRidesJob =
			viewModelScope.launch(Dispatchers.IO) {

				ridesRepository.fetchUserRides(
					authRepository.getLoggedInUser().uid,
					hosted = hostedOnly,
					wheelsType = wheelsType
				).cancellable().collect { result ->

					if (result.isSuccess) {
						_userRides.postValue(result.getOrNull())
						Log.d(TAG, "fetchUserRides: ${result.getOrNull()}")
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun searchRides(
		source: CustomAddress, destination: CustomAddress, date: CustomDate,
	) {
		_query = SearchRideQuery(
			userId = authRepository.getLoggedInUser().uid,
			source = source,
			destination = destination,
			date = date
		)
		fetchSearchedRidesJob?.cancel()
		fetchSearchedRides(query!!)
	}

	private fun fetchSearchedRides(query: SearchRideQuery) {
		fetchSearchedRidesJob =
			viewModelScope.launch(Dispatchers.IO) {
				ridesRepository.searchRides(query).cancellable().collect { result ->

					if (result.isSuccess) {
						_searchedRides.postValue(result.getOrNull())
						Log.d(TAG, "fetchSearchedRides: ${result.getOrNull()}")
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun restartSearch() {
		_requestResult.value = FormResult()
		_query = null
		fetchSearchedRidesJob?.cancel()
		_searchedRides.postValue(emptyList())
		_selectedSearchedRideId.postValue("")
	}

	fun startChatsUpdates() {
		fetchChatsJob?.cancel()
		fetchChats(authRepository.getLoggedInUser().uid)
	}

	fun stopChatsUpdates() {
		fetchChatsJob?.cancel()
	}

	private fun fetchChats(userId: String) {
		fetchChatsJob =
			viewModelScope.launch(Dispatchers.IO) {
				chatRepository.fetchChats(userId).cancellable().collect { result ->

					if (result.isSuccess) {
						_chats.postValue(result.getOrNull())
						Log.d(TAG, "Chats: ${result.getOrNull()}")
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun startChatUpdates(chatId: String) {
		_messages.postValue(emptyList())
		fetchChat(chatId)
	}

	fun stopChatUpdates() {
		fetchChatJob?.cancel()
	}

	private fun fetchChat(chatId: String) {
		fetchChatJob =
			viewModelScope.launch(Dispatchers.IO) {
				chatRepository.fetchChat(chatId).cancellable().collect { result ->

					if (result.isSuccess) {
						_messages.postValue(result.getOrNull())
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	fun sendMessage(text: String, date: String) {

		val name = authRepository.getLoggedInUser().let {
			it.name + " " + it.lastName
		}

		viewModelScope.launch(Dispatchers.IO) {
			val message = Message(
				uid = authRepository.getLoggedInUser().uid,
				name = name,
				message = text,
				date = date
			)

			chatRepository.sendMessage(
				chatId = _selectedChatId.value!!,
				message = message
			)
		}
	}

	fun startNotificationsUpdates() {
		fetchNotificationsJob?.cancel()
		fetchNotifications(
			authRepository.getLoggedInUser().uid
		)
	}

	fun stopNotificationsUpdates() {
		fetchNotificationsJob?.cancel()
	}

	private fun fetchNotifications(userId: String) {

		fetchNotificationsJob =
			viewModelScope.launch(Dispatchers.IO) {
				notificationRepository.fetchNotifications(userId).cancellable().collect { result ->

					if (result.isSuccess) {
						showNotification(result.getOrNull()!!)
					} else {
						Log.e(TAG, "Error: ${result.exceptionOrNull()}")
					}
				}
			}
	}

	private fun showNotification(notification: CustomNotification) {

		val oneTimeID = SystemClock.uptimeMillis().toInt()

		val builder = NotificationCompat.Builder(getApplication(), CHANNEL_ID)
			.setContentTitle(notification.title)
			.setContentText(notification.content)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			//.setPriority(NotificationCompat.PRIORITY_DEFAULT)

		with (NotificationManagerCompat.from(getApplication())) {
			// notificationId is a unique int for each notification that you must define
			notify(oneTimeID, builder.build())
		}
	}
}

@Suppress("UNCHECKED_CAST")
class SharedViewModelFactory(
	private val app: Application,
) : ViewModelProvider.AndroidViewModelFactory(app) {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
			return SharedViewModel(app) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
