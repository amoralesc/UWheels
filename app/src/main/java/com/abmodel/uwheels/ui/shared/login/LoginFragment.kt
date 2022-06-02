package com.abmodel.uwheels.ui.shared.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentLoginBinding
import com.abmodel.uwheels.ui.shared.data.SharedViewModel
import com.abmodel.uwheels.ui.shared.data.SharedViewModelFactory

class LoginFragment : Fragment() {

	companion object {
		const val TAG = "LoginFragment"
	}

	// Binding objects to access the view elements
	private var _binding: FragmentLoginBinding? = null
	private val binding get() = _binding!!

	private val loginViewModel: LoginViewModel by viewModels()
	private val sharedViewModel: SharedViewModel by activityViewModels {
		SharedViewModelFactory(requireActivity().application)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		// Inflate the layout and binding for this fragment
		_binding = FragmentLoginBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			password.setOnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					loginViewModel.login(
						binding.email.text.toString(),
						binding.password.text.toString()
					)
				}
				false
			}

			login.setOnClickListener {
				loginViewModel.login(
					binding.email.text.toString(),
					binding.password.text.toString()
				)
			}

			signUp.setOnClickListener {
				findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
			}
		}

		loginViewModel.loginResult.observe(viewLifecycleOwner) { loginResult ->
			loginResult ?: return@observe
			loginResult.message?.let {
				showLoginFailed(it)
			}
			if (loginResult.success) {
				loginIsSuccessful()
			}
		}
	}

	private fun showLoginFailed(@StringRes errorString: Int) {
		Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
	}

	private fun loginIsSuccessful() {
		sharedViewModel.startChatsUpdates()
		sharedViewModel.startNotificationsUpdates()
		sharedViewModel.restartUserRidesUpdates()
		findNavController().navigate(
			R.id.action_loginFragment_to_passengerHomeFragment
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
