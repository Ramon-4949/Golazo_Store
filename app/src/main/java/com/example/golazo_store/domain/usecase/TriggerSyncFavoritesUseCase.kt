package com.example.golazo_store.domain.usecase

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.golazo_store.data.worker.SyncFavoritesWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TriggerSyncFavoritesUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<SyncFavoritesWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork( // replace multiple requests with single pending
            "SyncFavoritesWork",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
