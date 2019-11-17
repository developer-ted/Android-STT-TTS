package com.ted.stt_tts.converter

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

class TextToSpeechConverter(
    private val responseListener: ConverterResponseListener,
    private val pitch: Float,
    private val speedRate: Float): SpeechTextConverterManager.ConverterListener {

    private val TAG = TextToSpeechConverter::class.java.name

    private var textToSpeech: TextToSpeech? = null

    override fun initialize(context: Context, message: String, locale: Locale): SpeechTextConverterManager.ConverterListener {
        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.apply {
                    language = Locale.getDefault()
                    setPitch(pitch)
                    setSpeechRate(speedRate)
                    language = locale
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(message)
                } else {
                    ttsUnder20(message)
                }
            } else {
                responseListener.onErrorOccurred("Failed to initialize TTS engine")
            }
        })
        return this
    }

    override fun onErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            TextToSpeech.ERROR -> "Generic error"
            TextToSpeech.ERROR_INVALID_REQUEST -> "Client side error, invalid request"
            TextToSpeech.ERROR_NOT_INSTALLED_YET -> "Insufficient download of the voice data"
            TextToSpeech.ERROR_NETWORK -> "Network error"
            TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            TextToSpeech.ERROR_OUTPUT -> "Failure in to the output (audio device or a file)"
            TextToSpeech.ERROR_SYNTHESIS -> "Failure of a TTS engine to synthesize the given input."
            TextToSpeech.ERROR_SERVICE -> "error from server"
            else -> "Didn't understand, please try again."
        }
    }



    private fun finish() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
    }

    private fun ttsUnder20(text: String) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"

        textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String) {
                Log.d(TAG, "started listening")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                super.onError(utteranceId, errorCode)
                responseListener.onErrorOccurred("Some Error Occurred "+ onErrorMessage(errorCode))
            }

            override fun onError(utteranceId: String) {
                responseListener.onErrorOccurred("Some Error Occurred $utteranceId")
            }

            override fun onDone(utteranceId: String) {
                //do some work here
                responseListener.onCompletion()
                finish()
            }
        })

        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, map)

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreater21(text: String) {
        val utteranceId = this.hashCode().toString() + ""
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

}