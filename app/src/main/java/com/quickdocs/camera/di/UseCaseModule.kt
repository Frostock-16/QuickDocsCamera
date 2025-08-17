package com.quickdocs.camera.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // Use cases are automatically provided by Hilt through constructor injection
    // No explicit provides methods needed since they have @Inject constructors
}