package com.example.golazo_store.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.domain.repository.UploadRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val api: GolazoApi,
    @ApplicationContext private val context: Context
) : UploadRepository {

    override suspend fun uploadImage(uri: Uri): Resource<String> {
        return try {
            val contentResolver = context.contentResolver
            
            var fileName = "temp_image.jpg"
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                }
            }

            val tempFile = File(context.cacheDir, fileName)
            tempFile.createNewFile()
            
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return Resource.Error("No se pudo leer la imagen seleccionada.")

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("imagen", tempFile.name, requestFile)

            val response = api.uploadImage(multipartBody)
            
            if (tempFile.exists()) tempFile.delete()

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.url)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Error de red al subir la imagen")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Excepción al subir la imagen")
        }
    }
}
