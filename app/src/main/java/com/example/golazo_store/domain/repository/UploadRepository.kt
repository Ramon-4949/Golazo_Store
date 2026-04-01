package com.example.golazo_store.domain.repository

import android.net.Uri
import com.example.golazo_store.domain.utils.Resource

interface UploadRepository {
    suspend fun uploadImage(uri: Uri): Resource<String>
}
