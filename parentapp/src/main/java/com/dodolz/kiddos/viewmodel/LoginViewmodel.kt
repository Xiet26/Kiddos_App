package com.dodolz.kiddos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewmodel: ViewModel() {
    
    private lateinit var firebaseAuth: FirebaseAuth
    
    private val _isLoginSuccessfull: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    private val _loginFailedException: MutableLiveData<Exception> by lazy {
        MutableLiveData<Exception>()
    }
    private val _userIsNotParent: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isLoginSuccessfull: LiveData<Boolean>
        get() = _isLoginSuccessfull
    val loginFailedException: LiveData<Exception>
        get() = _loginFailedException
    val userIsNotParent: LiveData<Boolean>
        get() = _userIsNotParent
    
    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuth = Firebase.auth
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener{ checkUserStatus(email) }
                .addOnFailureListener{
                    _isLoginSuccessfull.postValue(false)
                    _loginFailedException.postValue(it)
                }
        }
    }

    private fun checkUserStatus(email: String) {
        FirebaseFirestore.getInstance().collection("User").document(email).get()
            .addOnCompleteListener {
                if (it.result != null) {
                    val mStatus = it.result?.get("status")
                    mStatus?.let { status ->
                        when (status.toString()) {
                            "orangtua" -> _isLoginSuccessfull.postValue(true)
                            "anak" -> _userIsNotParent.postValue(true)
                        }
                    }
                }
            }
    }
}