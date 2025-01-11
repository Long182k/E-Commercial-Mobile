package com.example.e_commercial

import android.content.Context
import com.example.domain.model.UserDomainModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class EcommercialSession(private val context: Context) {

    fun storeUser(user: UserDomainModel) {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("id", user.id!!.toInt())
            putString("username", user.username)
            putString("email", user.email)
            putString("name", user.name)
            putString("avatarUrl", user.avatarUrl)
            apply()
        }
    }

    fun getUser(): UserDomainModel? {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val id = sharedPref.getInt("id", 0)
        val username = sharedPref.getString("username", null)
        val email = sharedPref.getString("email", null)
        val name = sharedPref.getString("name", null)
        val avatarUrl = sharedPref.getString("avatarUrl", null)
        
        return if (id != 0 && username != null && email != null && name != null) {
            val user = UserDomainModel(id, username, email, name, avatarUrl)
            user
        } else {
            null
        }
    }

    fun clearUser() {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("id")
            remove("username")
            remove("email")
            remove("name")
            remove("avatarUrl")
            apply()
        }
    }

    fun saveTheme(isDarkTheme: Boolean) {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isDarkTheme", isDarkTheme)
            apply()
        }
    }
    fun loadTheme(): Boolean {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isDarkTheme", false)
    }
}