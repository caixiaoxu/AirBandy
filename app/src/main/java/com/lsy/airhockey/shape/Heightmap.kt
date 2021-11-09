package com.lsy.airhockey.shape

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import com.lsy.airhockey.shape.programs.HeightmapShaderProgram
import kotlin.math.max
import kotlin.math.min

/**
 * @author Xuwl
 * @date 2021/10/27
 *
 */
class Heightmap(bitmap: Bitmap) {
    private val POSITION_COMPONENT_COUNT = 3
    private val width: Int
    private val height: Int
    private val numElements: Int
    private val vertexBuffer: VertexBuffer
    private val indexBuffer: IndexBuffer

    private val NORMAL_COMPONENT_COUNT = 3
    private val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT
    private val STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * Float.SIZE_BYTES

    init {
        width = bitmap.width
        height = bitmap.height

        if (width * height > 65536) {
            throw RuntimeException("Heightmap is to large for the index buffer.")
        }

        numElements = calculateNumElements()
        vertexBuffer = VertexBuffer(loadBitmapData(bitmap))
        indexBuffer = IndexBuffer(createIndexData())
    }

    private fun calculateNumElements(): Int = (width - 1) * (height - 1) * 2 * 3

    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val heightmapVertices = FloatArray(width * height * TOTAL_COMPONENT_COUNT)
        var offset = 0

        for (row in 0 until height) {
            for (col in 0 until width) {
                val point = getPoint(pixels, row, col)
                heightmapVertices[offset++] = point.x
                heightmapVertices[offset++] = point.y
                heightmapVertices[offset++] = point.z

                val top = getPoint(pixels, row - 1, col)
                val left = getPoint(pixels, row, col - 1)
                val right = getPoint(pixels, row, col + 1)
                val bottom = getPoint(pixels, row + 1, col)

                val rightToLeft = Geometry.vectorBetween(right, left)
                val topToBottom = Geometry.vectorBetween(top, bottom)
                val normal = rightToLeft.crossProduct(topToBottom).normalize()

                heightmapVertices[offset++] = normal.x
                heightmapVertices[offset++] = normal.y
                heightmapVertices[offset++] = normal.z
            }
        }
        return heightmapVertices
    }

    private fun getPoint(pixels: IntArray, row: Int, col: Int): Geometry.Point {
        val x = (col.toFloat() / (width - 1).toFloat()) - 0.5f
        val z = (row.toFloat() / (height - 1).toFloat()) - 0.5f

        var tRow = clamp(row, 0, width - 1)
        var tCol = clamp(col, 0, height - 1)
        val y = Color.red(pixels[(tRow * height) + tCol]).toFloat() / 255f
        return Geometry.Point(x, y, z)
    }

    private fun clamp(value: Int, minV: Int, maxV: Int): Int = max(minV, min(maxV, value))

    fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0
        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()
                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }
        return indexData
    }

    fun bindData(heightmapProgram: HeightmapShaderProgram) {
        vertexBuffer.setVertexAttribPointer(0,
            heightmapProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE)
        vertexBuffer.setVertexAttribPointer(POSITION_COMPONENT_COUNT * Float.SIZE_BYTES,
            heightmapProgram.aNormalLocation, NORMAL_COMPONENT_COUNT, STRIDE)
    }

    fun draw() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}