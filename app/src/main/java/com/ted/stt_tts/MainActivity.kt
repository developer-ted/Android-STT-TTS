package com.ted.stt_tts

import android.widget.Toast
import com.ted.stt_tts.converter.CONVERTER
import com.ted.stt_tts.converter.ConverterResponseListener
import com.ted.stt_tts.converter.SpeechTextConverterManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity: BasePermissionActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main
    override fun initLayout() {
        btnSpeechToText.setOnClickListener {
            SpeechTextConverterManager
                .instance
                .with(CONVERTER.STT, object: ConverterResponseListener {
                    override fun onSuccess(result: String) {
                        textSpeechToText.text = result
                    }
                    override fun onCompletion() {}
                    override fun onErrorOccurred(errorMessage: String) {
                        Toast.makeText(this@MainActivity, "ErrorMessage: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                })
                .initialize(this@MainActivity, "Speech Now!", Locale.KOREAN)
        }

        btnTextToSpeech.setOnClickListener {
            val textTTS = editTextToSpeech.text.toString()
            if (textTTS.isNotEmpty()) {
                SpeechTextConverterManager
                    .instance
                    .setTTSOption(1.0f, 1.0f)
                    .with(CONVERTER.TTS, object: ConverterResponseListener {
                        override fun onSuccess(result: String) {}
                        override fun onCompletion() {}
                        override fun onErrorOccurred(errorMessage: String) {
                            Toast.makeText(this@MainActivity, "ErrorMessage: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .initialize(this@MainActivity, textTTS, Locale.KOREAN)
            } else {
                Toast.makeText(this@MainActivity, "Please enter a message", Toast.LENGTH_SHORT).show()
            }

        }
    }

}
