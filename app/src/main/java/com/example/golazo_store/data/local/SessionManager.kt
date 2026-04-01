package com.example.golazo_store.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.golazo_store.domain.model.Usuario

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "golazo_session"
        const val KEY_ID = "USER_ID"
        const val KEY_NOMBRE = "USER_NOMBRE"
        const val KEY_CORREO = "USER_CORREO"
        const val KEY_ROL = "USER_ROL"
        const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    }

    fun saveUserSession(usuario: Usuario) {
        prefs.edit().apply {
            putInt(KEY_ID, usuario.id)
            putString(KEY_NOMBRE, usuario.nombre)
            putString(KEY_CORREO, usuario.correo)
            putString(KEY_ROL, usuario.rol)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserSession(): Usuario? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        return Usuario(
            id = prefs.getInt(KEY_ID, 0),
            nombre = prefs.getString(KEY_NOMBRE, "") ?: "",
            correo = prefs.getString(KEY_CORREO, "") ?: "",
            rol = prefs.getString(KEY_ROL, "Cliente") ?: "Cliente"
        )
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
