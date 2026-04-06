package com.example.golazo_store.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.golazo_store.domain.repository.FavoritesRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val favoritesRepository: FavoritesRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return when (favoritesRepository.syncFavorites()) {
            is Resource.Success -> Result.success()
            is Resource.Error -> Result.retry()
            else -> Result.failure()
        }
    }
}
