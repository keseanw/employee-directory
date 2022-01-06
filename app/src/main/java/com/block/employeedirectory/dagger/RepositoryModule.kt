package com.block.employeedirectory.dagger

import com.block.employeedirectory.data.CoroutinesDispatcherProvider
import com.block.employeedirectory.data.repository.DefaultEmployeeRepository
import com.block.employeedirectory.data.repository.EmployeeRepository
import com.block.employeedirectory.data.source.remote.EmployeeRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) //@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideEmployeeRepository(
        remoteDataSource: EmployeeRemoteDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): EmployeeRepository =  DefaultEmployeeRepository(remoteDataSource, dispatcherProvider)
}