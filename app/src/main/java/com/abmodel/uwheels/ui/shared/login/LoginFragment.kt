package com.abmodel.uwheels.ui.shared.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abmodel.uwheels.R
import com.abmodel.uwheels.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginFragment : Fragment() {

	// Binding objects to access the view elements
	private var _binding: FragmentLoginBinding? = null
	private val binding get() = _binding!!

	private val loginViewModel: LoginViewModel by viewModels()

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

		val usernameEditText = binding.email
		val passwordEditText = binding.password
		val loginButton = binding.login

		binding.apply {
			signUp.setOnClickListener {
				findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
			}
		}

		loginViewModel.loginFormState.observe(viewLifecycleOwner,
			Observer { loginFormState ->
				if (loginFormState == null) {
					return@Observer
				}
				// loginButton.isEnabled = loginFormState.isDataValid
				loginFormState.usernameError?.let {
					// usernameEditText.error = getString(it)
				}
				loginFormState.passwordError?.let {
					// passwordEditText.error = getString(it)
				}
			})

		loginViewModel.loginResult.observe(viewLifecycleOwner,
			Observer { loginResult ->
				loginResult ?: return@Observer
				loginResult.error?.let {
					showLoginFailed(it)
				}
				loginResult.success?.let {
					updateUiWithUser(it)
				}
			})

		val afterTextChangedListener = object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// ignore
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				// ignore
			}

			override fun afterTextChanged(s: Editable) {
				loginViewModel.loginDataChanged(
					usernameEditText.text.toString(),
					passwordEditText.text.toString()
				)
			}
		}
		usernameEditText.addTextChangedListener(afterTextChangedListener)
		passwordEditText.addTextChangedListener(afterTextChangedListener)
		passwordEditText.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				loginViewModel.login(
					usernameEditText.text.toString(),
					passwordEditText.text.toString()
				)
			}
			false
		}

		loginButton.setOnClickListener {
			loginViewModel.login(
				usernameEditText.text.toString(),
				passwordEditText.text.toString()
			)
		}

		//ALL LOGIC COMES HERE


		//configure google sign in
		val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build()

		//get context
		googleSingInClient = GoogleSignIn.getClient(requireContext(), gso)

		//call firebase
		firebaseAuth = FirebaseAuth.getInstance()
		checkUser()

		binding.loginGoogle.setOnClickListener {
			Toast.makeText(context, "Google Sign In", Toast.LENGTH_SHORT).show()
			val signInIntent = googleSingInClient.signInIntent
			startActivityForResult(signInIntent, RC_SIGN_IN)
		}


	}

	private fun updateUiWithUser(model: LoggedInUserView) {
		val welcome = getString(R.string.welcome) + model.displayName
		// TODO : initiate successful logged in experience
		Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
	}

	private fun showLoginFailed(@StringRes errorString: Int) {
		val appContext = context?.applicationContext ?: return
		Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	//DELETE IF CHAMGES ON GSING IN

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == 100){
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try{
				val account = task.getResult(ApiException::class.java)
				firebaseAuthWithGoogle(account!!)
			}catch (e: Exception){
			}
		}
	}

	private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
		val credential = GoogleAuthProvider.getCredential(account.idToken, null)
		firebaseAuth.signInWithCredential(credential)
			.addOnSuccessListener { authResult ->
				val user = firebaseAuth.currentUser
				val uid = user!!.uid
				val email = user!!.email

				//check if is a new user or existing user
				if (authResult.additionalUserInfo!!.isNewUser) {
					//Toast.makeText(this, "Welcome new user", Toast.LENGTH_SHORT).show()
				}else{
					//Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show()
				}
				//start profile activity
				findNavController().navigate(R.id.action_loginFragment_to_passengerHomeFragment)
			}

			.addOnFailureListener {
				//Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
			}
	}
}