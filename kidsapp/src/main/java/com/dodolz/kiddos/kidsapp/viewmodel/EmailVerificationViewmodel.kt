package com.dodolz.kiddos.kidsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EmailVerificationViewmodel: ViewModel() {
    
    private val _isUserVerified: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    private val _isEmailHasBeenSent: MutableLiveData<Pair<String, Boolean>> by lazy {
        MutableLiveData<Pair<String, Boolean>>()
    }
    
    val isUserVerified: LiveData<Boolean>
        get() = _isUserVerified
    val isEmailHasBeenSent: LiveData<Pair<String, Boolean>>
        get() = _isEmailHasBeenSent
    
    private fun taskReloadUser() = FirebaseAuth.getInstance().currentUser?.reload()
    
    fun checkIfEmailVerified() {
        taskReloadUser()?.addOnSuccessListener {
            FirebaseAuth.getInstance().currentUser?.let {
                if (!it.isEmailVerified) {
                    it.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) _isEmailHasBeenSent.postValue(Pair(it.email.toString(), true))
                        verifyUser()
                    }
                } else {
                    _isUserVerified.postValue(true)
                }
            }
        }
    }
    
    private fun verifyUser() = viewModelScope.launch(Dispatchers.IO) {
        var verifyFinished = false
        while (!verifyFinished) {
            taskReloadUser()?.addOnSuccessListener {
                FirebaseAuth.getInstance().currentUser?.let {
                    if (it.isEmailVerified) {
                        _isUserVerified.postValue(true)
                        verifyFinished = true
                    }
                }
            }
            delay(2000L)
        }
    }
}