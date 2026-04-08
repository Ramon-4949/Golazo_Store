package com.example.golazo_store.domain.usecase

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.golazo_store.data.worker.SyncCamisetasWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TriggerSyncCamisetasUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke() {
        val request = OneTimeWorkRequestBuilder<SyncCamisetasWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "SyncCamisetasWork",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
