package com.ted.stt_tts.converter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

class SpeechToTextConverter(private val responseListener: ConverterResponseListener): SpeechTextConverterManager.ConverterListener {

    private val TAG = SpeechToTextConverter::class.java.name

    private var intentSpeech: Intent? = null
    private var speechRecognizer: SpeechRecognizer? = null

    override fun initialize(context: Context, message: String, locale: Locale): SpeechTextConverterManager.ConverterListener {
        if (intentSpeech == null) {
            intentSpeech = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
                putExtra(RecognizerIntent.EXTRA_PROMPT, message)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }
        }

        //Add listeners
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(CustomRecognitionListener())
            }
        }
        startSTT()
        return this
    }

    override fun onErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
    }

    private fun startSTT() {
        if (intentSpeech != null) {
            speechRecognizer?.startListening(intentSpeech)
        }
    }

    fun stopTTS() {
        speechRecognizer?.stopListening()
    }


    internal inner class CustomRecognitionListener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech")
        }

        override fun onError(error: Int) {
            Log.e(TAG, "error $error")
            responseListener.onErrorOccurred(onErrorMessage(error))
        }

        override fun onResults(results: Bundle) {
            var translateResults = String()
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (data != null) {
                for ( result in  data) {
                    translateResults += result + "\n"
                }
            }
            responseListener.onSuccess(translateResults)
        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d(TAG, "onPartialResults")
        }

        override  fun onEvent(eventType: Int, params: Bundle) {
            Log.d(TAG, "onEvent $eventType")
        }
    }

}


