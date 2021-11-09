package com.lsy.airhockey.shape

import com.lsy.airhockey.shape.programs.ColorShaderProgram

class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) {
    companion object {
        val POSITION_COMPONENT_COUNT = 3
    }

    private val vertexArray: VertexArray
    private val drawList: List<ObjectBuilder.DrawCommand>

    init {
        val generatedData = ObjectBuilder.createPuck(
            Geometry.Cylinder(Geometry.Point(0f, 0f, 0f), radius, height), numPointsAroundPuck
        )
        vertexArray = VertexArray(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    fun draw() {
        drawList.forEach {
            it.draw()
        }
    }
}