package com.llmhub.llmhub.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {
    // ТВІЙ API-ключ. ПІЗНІШЕ ЗАМІНИМО НА БЕЗПЕЧНИЙ ВАРІАНТ (наприклад, введення в налаштуваннях)
    private const val API_KEY = "AIzaSyDfkkHWQkQPGkI02elKCehdeZ-xW3qWKrE"

    // Модель gemini-1.5-flash — швидка, розумна, підтримує українську, має доступ до інтернету через Google
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    /**
     * Надсилає запит до Gemini і повертає текстову відповідь
     */
    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            response.text?.trim() ?: "Вибач, Діана не зрозуміла..."
        } catch (e: Exception) {
            "Помилка зв'язку з Діаною: ${e.localizedMessage ?: "невідома помилка"}"
        }
    }
}
