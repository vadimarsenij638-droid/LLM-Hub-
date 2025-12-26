package com.llmhub.llmhub.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TTSManager(context: Context) {
    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
            // Встановлюємо українську мову
            val result = tts.setLanguage(Locale("uk", "UA"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Якщо української немає — fallback на англійську, але з високим тоном
                tts.language = Locale.US
            }

            // Налаштування голосу "дівчинки"
            tts.setPitch(1.5f)    // Вищий тон (1.0 — нормальний, 1.5 — явно жіночий/дитячий)
            tts.setSpeechRate(0.95f)  // Трохи повільніше для природності

            // Опціонально: можна додати слухача для завершення промови
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}
            })
        }
    }

    /**
     * Проголошує текст вголос
     */
    fun speak(text: String) {
        if (text.isNotBlank()) {
            tts.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "diana_utterance")
        }
    }

    /**
     * Зупиняє промову
     */
    fun stop() {
        tts.stop()
    }

    /**
     * Звільняє ресурси (викликати в onDestroy)
     */
    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
