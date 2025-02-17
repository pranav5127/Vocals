package com.app.sounds.utils.record

import java.io.File

interface AudioRecorder {
    fun recordAudio(outputFile: File)
    fun stop()
}