package com.example.golazo_store.di

import android.content.Context
import androidx.room.Room
import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.database.GolazoDatabase
import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.remotedatasource.CamisetaRemoteDataSource
import com.example.golazo_store.data.repository.CamisetaRepositoryImpl
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGolazoDatabase(
        @ApplicationContext context: Context
    ): GolazoDatabase {
        return Room.databaseBuilder(
            context,
            GolazoDatabase::class.java,
            "golazo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCamisetaDao(database: GolazoDatabase): CamisetaDao {
        return database.camisetaDao()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideGolazoApi(moshi: Moshi): GolazoApi {
        return Retrofit.Builder()
            .baseUrl("http://golazostoreapi.somee.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GolazoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCamisetaRepository(
        api: GolazoApi,
        dao: CamisetaDao
    ): CamisetaRepository {
        val remoteDataSource = CamisetaRemoteDataSource(api)
        return CamisetaRepositoryImpl(remoteDataSource, dao)
    }
}