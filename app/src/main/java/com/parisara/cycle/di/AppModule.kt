package com.parisara.cycle.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.parisara.cycle.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance().apply {
            try {
                setPersistenceEnabled(true)
            } catch (e: Exception) {
                // Already initialized
            }
        }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext ctx: Context
    ): AppDatabase = AppDatabase.getInstance(ctx)

    // ✅ PlacesClient REMOVED — using OSMDroid instead
}