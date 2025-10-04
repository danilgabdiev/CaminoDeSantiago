package com.example.caminodesantiago

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Repository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadMonasteries(): List<Monastery> = withContext(Dispatchers.IO) {
        val text = context.assets.open("monasteries.json").bufferedReader().use { it.readText() }
        json.decodeFromString(text)
    }

    suspend fun loadRoutes(): List<Route> = withContext(Dispatchers.IO) {
        // Если routes.json отсутствует — возвращаем пустой список
        try {
            val text = context.assets.open("routes.json").bufferedReader().use { it.readText() }
            json.decodeFromString(text)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
