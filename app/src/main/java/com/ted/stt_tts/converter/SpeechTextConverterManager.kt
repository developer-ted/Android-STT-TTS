package com.ted.stt_tts.converter

import android.content.Context
import java.util.*

class SpeechTextConverterManager {

    var ttsPitch = 1.0f
    var ttsSpeechRate = 1.0f

    fun setTTSOption(pitch: Float = 1.0f, speechRate: Float = 1.0f): SpeechTextConverterManager {
        ttsPitch = pitch
        ttsSpeechRate = speechRate
        return this
    }

    fun with(converter: CONVERTER, listener: ConverterResponseListener): ConverterListener {
        return when (converter) {
            CONVERTER.STT -> {
                SpeechToTextConverter(listener)
            }
            CONVERTER.TTS -> {
                TextToSpeechConverter(listener, ttsPitch, ttsSpeechRate)
            }
        }
    }

    companion object {
        val instance = SpeechTextConverterManager()
    }

    interface ConverterListener {
        fun initialize(context: Context, message: String, locale: Locale = Locale.KOREAN): ConverterListener
        fun onErrorMessage(errorCode: Int): String
    }
}


enum class CONVERTER {
    TTS, STT
}

