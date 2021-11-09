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
class VertexBuffer(vertexData: FloatArray) {
    private val bufferId: Int

    init {
        val buffers = IntArray(1)
        GLES20.glGenBuffers(buffers.size, buffers, 0)
        if (0 == buffers[0]) {
            throw RuntimeException("Could not create a new vertex buffer object.")
        }
        bufferId = buffers[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0])
        val vertexArray = ByteBuffer.allocateDirect(vertexData.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        vertexArray.position(0)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexArray.capacity() * Float.SIZE_BYTES,
            vertexArray, GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    /**
     * 设置顶点参数
     * @param dataOffsset 起始位置
     * @param attributeLocation 着色器变量
     * @param componentCount 一条数据有几个数据
     * @param stride 长度
     */
    fun setVertexAttribPointer(
        dataOffsset: Int, attributeLocation: Int, componentCount: Int, stride: Int,
    ) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId)
        GLES20.glVertexAttribPointer(attributeLocation,
            componentCount, GLES20.GL_FLOAT, false, stride, dataOffsset)
        GLES20.glEnableVertexAttribArray(attributeLocation)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }
}