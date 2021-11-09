package com.lsy.airhockey.shape

import com.lsy.airhockey.shape.programs.ColorShaderProgram

class Mallet1(val radius: Float, val height: Float, numPointsAroundMallet: Int) {
    private val POSITION_COMPONENT_COUNT = 3

    private val vertexArray: VertexArray
    private val drawList: List<ObjectBuilder.DrawCommand>

    init {
        val generatedData = ObjectBuilder.createMallet(
            Geometry.Point(0f, 0f, 0f), radius, height, numPointsAroundMallet
        )
        vertexArray = VertexArray(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    /**
     * 绑定数据
     * @param textureProgram 颜色程序
     */
    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0, colorProgram.aPositionLocation, POSITION_COMPONENT_COUNT, 0
        )
    }

    /**
     * 绘制
     */
    fun draw() {
        drawList.forEach {
            it.draw()
        }
    }
}