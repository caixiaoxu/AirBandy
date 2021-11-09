package com.lsy.airhockey.shape

import android.opengl.GLES20
import android.util.FloatMath
import kotlin.math.cos
import kotlin.math.sin

class ObjectBuilder(sizeInVertices: Int) {
    companion object {
        val FLOATS_PRE_VERTEX = 3

        /**
         * 圆柱体的顶点数量,圆心 + （num + 1），第一点重叠，圆才能闭合
         */
        fun sizeOfCircleInVertices(numPoints: Int): Int = 1 + (numPoints + 1)

        /**
         * 圆体侧面的顶点数量
         */
        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int = (numPoints + 1) * 2

        /**
         * 创建冰球
         * @param puck 圆柱体参数
         * @param numPoints 圆分成多少数量
         */
        fun createPuck(puck: Geometry.Cylinder, numPoints: Int): GeneratedData {
            //总的顶点数量
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)
            //参数封装
            val builder = ObjectBuilder(size)
            //顶部圆参数
            val puckTop = Geometry.Circle(puck.center.translatY(puck.height / 2f), puck.radius)
            //组装
            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)
            //生成
            return builder.build()
        }

        /**
         * 创建木槌
         * @param center 中心点
         * @param radius 半径
         * @param height 高度
         * @param numPoints 圆分成多少数量
         */
        fun createMallet(
            center: Geometry.Point, radius: Float, height: Float, numPoints: Int
        ): GeneratedData {
            //总的顶点数量
            val size =
                (sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)) * 2
            //封装
            val builder = ObjectBuilder(size)
            //基部高度
            val baseHeight = height * 0.25f
            val baseCircle = Geometry.Circle(center.translatY(-baseHeight), radius)
            val baseCylinder =
                Geometry.Cylinder(baseCircle.center.translatY(-baseHeight / 2f), radius, baseHeight)
            //组装
            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)
            //手柄的高度和半径
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f
            val handleCircle = Geometry.Circle(center.translatY(height * 0.5f), handleRadius)
            val handleCylinder = Geometry.Cylinder(
                handleCircle.center.translatY(-handleHeight / 2f), handleRadius, handleHeight
            )
            //组装
            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)
            return builder.build()
        }
    }

    //顶点数组
    private val vertexData: FloatArray
    private var offset = 0

    private val drawList = ArrayList<DrawCommand>()

    init {
        vertexData = FloatArray(sizeInVertices * FLOATS_PRE_VERTEX)
    }

    /**
     * 添加圆
     * @param circle 圆的参数
     * @param numPoints 顶点数量
     */
    fun appendCircle(circle: Geometry.Circle, numPoints: Int) {
        //起始点位置
        val startVertex = offset / FLOATS_PRE_VERTEX
        //顶点数量
        val numVertices = sizeOfCircleInVertices(numPoints)

        //圆心
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        //圆周上的点
        for (i in 0..numPoints) {
            val angleInRadians = (i / numPoints.toFloat()) * (Math.PI.toFloat() * 2f)

            vertexData[offset++] = circle.center.x + circle.radius * cos(angleInRadians)
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z + circle.radius * sin(angleInRadians)
        }
        //绘制
        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    /**
     * 添加圆带
     * @param puck 圆带参数
     * @param numPoints 顶点数量
     */
    fun appendOpenCylinder(cylinder: Geometry.Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PRE_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)

        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)


        for (i in 0..numPoints) {
            val angleInRadians = (i / numPoints.toFloat()) * (Math.PI.toFloat() * 2f)
            val xPosition = cylinder.center.x + cylinder.radius * cos(angleInRadians)
            val zPosition = cylinder.center.z + cylinder.radius * sin(angleInRadians)

            //上面的点
            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition

            //下面的点
            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    fun build(): GeneratedData = GeneratedData(vertexData, drawList)

    interface DrawCommand {
        fun draw()
    }

    class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>) {

    }
}