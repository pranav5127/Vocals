package com.app.sounds.utils.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

class AndroidAudioRecorder(private val context: Context):AudioRecorder {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingThread: Thread? = null

    var amplitudeListener: ((Float) -> Unit)? = null

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    override fun recordAudio(outputFile: File) {
        if (!hasMicrophonePermission()) {
            throw SecurityException("Microphone permission is required!")
        }

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            throw IllegalStateException("Invalid buffer size!")
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize
            )

            audioRecord?.startRecording()
            isRecording = true

            recordingThread = Thread {
                writeAudioDataToFile(outputFile, minBufferSize)
            }.apply { start() }

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun writeAudioDataToFile(outputFile: File, bufferSize: Int) {
        val pcmFile = File(outputFile.absolutePath + ".pcm")
        val wavFile = File(outputFile.absolutePath)

        FileOutputStream(pcmFile).use { fileOutputStream ->
            val buffer = ByteArray(bufferSize)

            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0) {
                    fileOutputStream.write(buffer, 0, bytesRead)

                    // Calculate amplitude and send it to the listener
                    val amplitude = calculateAmplitude(buffer)
                    amplitudeListener?.invoke(amplitude.toFloat())
                }
            }
        }

        convertPcmToWav(pcmFile, wavFile)
    }

    override fun stop() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingThread = null
    }

    private fun calculateAmplitude(buffer: ByteArray): Double {
        var sum = 0.0
        for (i in buffer.indices step 2) {
            val sample = (buffer[i].toInt() or (buffer[i + 1].toInt() shl 8)).toShort()
            sum += sample * sample
        }
        val rms = sqrt(sum / (buffer.size / 2))
        return rms / 32767f
    }

    private fun convertPcmToWav(pcmFile: File, wavFile: File) {
        val pcmData = pcmFile.readBytes()
        val totalAudioLen = pcmData.size.toLong()
        val totalDataLen = totalAudioLen + 36
        val byteRate = SAMPLE_RATE * 2

        FileOutputStream(wavFile).use { out ->
            out.write("RIFF".toByteArray())
            out.write(intToByteArray(totalDataLen.toInt()))
            out.write("WAVE".toByteArray())
            out.write("fmt ".toByteArray())
            out.write(intToByteArray(16))
            out.write(shortToByteArray(1))
            out.write(shortToByteArray(1))
            out.write(intToByteArray(SAMPLE_RATE))
            out.write(intToByteArray(byteRate))
            out.write(shortToByteArray(2))
            out.write(shortToByteArray(16))
            out.write("data".toByteArray())
            out.write(intToByteArray(totalAudioLen.toInt()))
            out.write(pcmData)
        }
        pcmFile.delete()
    }

    private fun intToByteArray(value: Int): ByteArray =
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()

    private fun shortToByteArray(value: Short): ByteArray =
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()

    private fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }
}
