package com.lsy.airhockey.shape

import android.opengl.GLES20
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author Xuwl
 * @date 2021/10/27
 *
 */
class IndexBuffer(vertexData: ShortArray) {
    val bufferId: Int

    init {
        val buffers = IntArray(1)
        GLES20.glGenBuffers(buffers.size, buffers, 0)
        if (0 == buffers[0]) {
            throw RuntimeException("Could not create a new vertex buffer object.")
        }
        bufferId = buffers[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0])
        val vertexArray = ByteBuffer.allocateDirect(vertexData.size * Short.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(vertexData)
        vertexArray.position(0)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexArray.capacity() * Short.SIZE_BYTES,
            vertexArray, GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }
}