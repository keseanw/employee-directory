package com.block.employeedirectory.dagger

import com.block.employeedirectory.Constants
import com.block.employeedirectory.data.api.EmployeeService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Networking
 */


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideEmployeeService(): EmployeeService {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.BASE_URL)
            .build()
        return retrofit.create(EmployeeService::class.java)
    }
}