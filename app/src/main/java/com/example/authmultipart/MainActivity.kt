package com.example.authmultipart

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.authmultipart.databinding.ActivityMainBinding
import com.example.authmultipart.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private lateinit var registerViewModel : RegisterViewModel
    private val binding get() = _binding!!
    private lateinit var uri : Uri
    private var getFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener {
            openGallery()
        }
        binding.btnPost.setOnClickListener {
            upload()
        }
    }

    private fun createFile(context: Context): File {
        return File.createTempFile(
            "IMG_",
            ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createFile(context)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null){
            val myFile = uriToFile(it, this)
            getFile = myFile
            binding.imgUser.setImageURI(it)
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    private fun openGallery(){
        intent.type = "image/*"
        openGallery.launch("image/*")
    }


    private fun upload(){
        if(getFile != null){
            val file = reduceFileImage(getFile as File)

            val fullname = binding.etFullname.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val email = binding.etEmail.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val password = binding.etPassword.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val city = binding.etCity.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val address = binding.etAddress.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber = binding.etPhoneNumber.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                currentImageFile
            )
            registerViewModel.userRegister(fullname,email,password,phoneNumber,address,imageMultipart,city)
            registerViewModel.doRegisterObserver().observe(this){
                if(it != null){
                    registerViewModel.message().observe(this){ its ->
                        Toast.makeText(this@MainActivity, its, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    registerViewModel.message().observe(this){ its ->
                        Toast.makeText(this@MainActivity, its, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}