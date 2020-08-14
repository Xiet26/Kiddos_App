package com.dodolz.kiddos.kidsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    
    val isLoginSuccessfull: LiveData<Boolean>
        get() = _isLoginSuccessfull
    val loginFailedException: LiveData<Exception>
        get() = _loginFailedException
    
    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener{ _isLoginSuccessfull.postValue(true) }
                .addOnFailureListener{
                    _isLoginSuccessfull.postValue(false)
                    _loginFailedException.postValue(it)
                }
        }
        
    }
}