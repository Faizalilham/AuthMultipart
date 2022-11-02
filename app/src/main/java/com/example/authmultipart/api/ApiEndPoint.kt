package com.example.authmultipart.api

import com.example.authmultipart.model.Register
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {

    @Multipart
    @POST("auth/register")
    fun postData(@Part("full_name") full_name : RequestBody,
                 @Part("email") email : RequestBody,
                 @Part("password") password : RequestBody,
                 @Part("phone_number") phone_number : RequestBody,
                 @Part("address") address : RequestBody,
                 @Part image : MultipartBody.Part,
                 @Part("city") city : RequestBody):Call<Register>

}

