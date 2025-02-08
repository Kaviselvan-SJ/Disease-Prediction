package com.kavi.diseaseprediction

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteModelInterpreter(context: Context, modelName: String) {
    private val interpreter: Interpreter

    init {
        val model = loadModelFile(context, modelName)
        interpreter = Interpreter(model)
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(inputData: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(inputData.size * 4).apply {
            order(ByteOrder.nativeOrder())
            inputData.forEach { putFloat(it) }
        }

        val outputBuffer = ByteBuffer.allocateDirect(4).apply {
            order(ByteOrder.nativeOrder())
        }

        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()

        val outputData = FloatArray(1)
        outputBuffer.asFloatBuffer().get(outputData)
        return outputData
    }
}
