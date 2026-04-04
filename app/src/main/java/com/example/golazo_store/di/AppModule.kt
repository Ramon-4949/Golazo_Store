package com.example.golazo_store.di

import android.content.Context
import androidx.room.Room
import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.database.GolazoDatabase
import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.data.remote.api.AuthApi
import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.remotedatasource.AuthRemoteDataSource
import com.example.golazo_store.data.remote.remotedatasource.CamisetaRemoteDataSource
import com.example.golazo_store.data.repository.AuthRepositoryImpl
import com.example.golazo_store.data.repository.CamisetaRepositoryImpl
import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.data.local.dao.CartDao
import com.example.golazo_store.domain.repository.CartRepository
import com.example.golazo_store.data.repository.CartRepositoryImpl
import com.example.golazo_store.domain.repository.UploadRepository
import com.example.golazo_store.data.repository.UploadRepositoryImpl
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.repository.PedidoRepository
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
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    @Singleton
    fun provideCamisetaDao(database: GolazoDatabase): CamisetaDao {
        return database.camisetaDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: GolazoDatabase): CartDao {
        return database.cartDao()
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
    fun provideAuthApi(moshi: Moshi): AuthApi {
        return Retrofit.Builder()
            .baseUrl("http://golazostoreapi.somee.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        sessionManager: SessionManager
    ): AuthRepository {
        val remoteDataSource = AuthRemoteDataSource(api)
        return AuthRepositoryImpl(remoteDataSource, sessionManager)
    }

    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ): SessionManager {
        return SessionManager(context)
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

    @Provides
    @Singleton
    fun provideCartRepository(
        dao: CartDao
    ): CartRepository {
        return CartRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDireccionRepository(
        api: GolazoApi
    ): com.example.golazo_store.domain.repository.DireccionRepository {
        val remoteDataSource = com.example.golazo_store.data.remote.remotedatasource.DireccionRemoteDataSource(api)
        return com.example.golazo_store.data.repository.DireccionRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideMetodoPagoRepository(
        api: GolazoApi
    ): com.example.golazo_store.domain.repository.MetodoPagoRepository {
        val remoteDataSource = com.example.golazo_store.data.remote.remotedatasource.MetodoPagoRemoteDataSource(api)
        return com.example.golazo_store.data.repository.MetodoPagoRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideCategoriaRepository(
        api: GolazoApi
    ): com.example.golazo_store.domain.repository.CategoriaRepository {
        return com.example.golazo_store.data.repository.CategoriaRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUploadRepository(
        api: GolazoApi,
        @ApplicationContext context: Context
    ): UploadRepository {
        return UploadRepositoryImpl(api, context)
    }

    @Provides
    @Singleton
    fun providePedidoRepository(
        remoteDataSource: com.example.golazo_store.data.remote.remotedatasource.PedidoRemoteDataSource
    ): PedidoRepository {
        return com.example.golazo_store.data.repository.PedidoRepositoryImpl(remoteDataSource)
    }
}