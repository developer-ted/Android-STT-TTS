package com.ted.stt_tts.converter

interface ConverterResponseListener {
    fun onSuccess(result: String)
    fun onCompletion()
    fun onErrorOccurred(errorMessage: String)
}