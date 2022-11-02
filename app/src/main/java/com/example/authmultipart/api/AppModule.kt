package com.example.githubapiremake.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.authmultipart.api.ApiEndPoint
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL_AUTH = "https://market-final-project.herokuapp.com/" // OAUTH WITH BEARER TOKEN

    @Provides
    @Singleton
    fun okHttpClient():OkHttpClient{
        return OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build()
    }


    @Singleton
    @Provides
    fun setupRetrofit(okHttp : OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun apiEndPoint(retrofit : Retrofit):ApiEndPoint = retrofit.create(ApiEndPoint::class.java)

}