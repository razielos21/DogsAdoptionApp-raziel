package com.example.dogsadoptionapp.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*

object TranslationHelper {
    private lateinit var prefs: android.content.SharedPreferences
    private val memoryCache = mutableMapOf<String, String>()
    private val client = OkHttpClient()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("breed_translations", Context.MODE_PRIVATE)
    }

    suspend fun translateToHebrew(text: String): String = withContext(Dispatchers.IO) {
        val deviceLang = Locale.getDefault().language
        if (deviceLang != "he" && deviceLang != "iw") {
            return@withContext text
        }

        memoryCache[text]?.let { return@withContext it }

        prefs.getString(text, null)?.let {
            memoryCache[text] = it
            return@withContext it
        }

        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val url = Constants.TRANSLATE_URL + "?q=$encodedText&langpair=en|he"

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "DogsAdoptionApp/1.0")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("MyMemoryAPI", "Response: $responseBody")

            val translated = JSONObject(responseBody ?: "")
                .getJSONObject("responseData")
                .getString("translatedText")

            memoryCache[text] = translated
            prefs.edit().putString(text, translated).apply()

            return@withContext translated
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext text
        }
    }
}
