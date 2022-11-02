package com.example.authmultipart.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authmultipart.model.Register
import com.example.authmultipart.api.ApiEndPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val api : ApiEndPoint):ViewModel() {

    private val doRegister : MutableLiveData<Register> = MutableLiveData()
    fun doRegisterObserver():LiveData<Register> = doRegister

    private val _message : MutableLiveData<String> = MutableLiveData()
    fun message():LiveData<String> = _message


    fun userRegister(fullname :RequestBody,
                   email :RequestBody,
                   password : RequestBody,
                   phone_number : RequestBody,
                   address :RequestBody,
                   image : MultipartBody.Part,
                    city : RequestBody){

        api.postData(fullname,email,password,phone_number,address,image,city)
            .enqueue(object : Callback<Register>{
                override fun onResponse(call: Call<Register>, response: Response<Register>) {
                    if(response.isSuccessful){
                        val body = response.body()
                        if(body != null){
                            doRegister.postValue(body)
                            _message.postValue("Register Successfully")
                            Log.d("SUCCESS",body.toString())
                        }else{
                            doRegister.postValue(null)
                            _message.postValue("Register Failed => Body is null")
                            Log.d("Failed","NULL")
                        }
                    }else{
                        Log.d("Failed","${response.code()}")
                        _message.postValue("Register Failed -> ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Register>, t: Throwable) {
                    doRegister.postValue(null)
                    _message.postValue("Register Failed => ${t.message}")
                    Log.d("Failed","${t.message}")
                }

            })
    }


}