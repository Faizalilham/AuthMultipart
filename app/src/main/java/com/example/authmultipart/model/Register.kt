package com.example.authmultipart.model

data class Register(
    val id : Int,
    val full_name : String,
    val email : String,
    val password : String,
    val phone_number :Int,
    val address : String,
    val image_url :String,
    val city : String,
    val createdAt : String,
    val updatedAt : String
)