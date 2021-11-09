package com.lsy.airhockey.shape

import android.graphics.Color
import android.opengl.GLES20
import com.lsy.airhockey.shape.programs.ParticleShaderProgram

/**
 * @author Xuwl
 * @date 2021/10/26
 *
 */
class ParticleSystem(val maxParticleCount: Int) {
    private val POSITION_COMPONENT_COUNT = 3
    private val COLOR_COMPONENT_COUNT = 3
    private val VECTOR_COMPONENT_COUNT = 3
    private val PARTICLE_START_TIME_COMPONENT_COUNT = 3

    private val TOTAL_COMPONENT_COUNT =
        POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT

    private val STRIDE = TOTAL_COMPONENT_COUNT * Float.SIZE_BYTES

    private val particles: FloatArray
    private val vertexArray: VertexArray

    private var currentParticleCount = 0
    private var nextParticle = 0

    init {
        particles = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
        vertexArray = VertexArray(particles)
    }

    /**
     * 添加粒子
     * @param position 位置
     * @param color 颜色
     * @param direction 方向
     * @param particlesStartTime 创建时间
     */
    fun addParticle(
        position: Geometry.Point,
        color: Int,
        direction: Geometry.Vector,
        particlesStartTime: Float,
    ) {
        //偏移位置
        val particleOffset = nextParticle * TOTAL_COMPONENT_COUNT
        var currentOffset = particleOffset
        //在最大粒子数内
        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }
        //下一个就达到最大粒子数就从头开始
        nextParticle++
        if (nextParticle == maxParticleCount) {
            nextParticle = 0
        }

        //加入数组
        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset++] = particlesStartTime

        //更新顶点缓存数据
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    /**
     * 绑定数据
     * @param particleProgram 粒子着色器程序
     */
    fun bindData(particleProgram: ParticleShaderProgram) {
        //顶点
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE)

        //颜色
        dataOffset += POSITION_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.aColorLocation, COLOR_COMPONENT_COUNT, STRIDE)

        //方向
        dataOffset += COLOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.aDirectionVectorLocation, VECTOR_COMPONENT_COUNT, STRIDE)

        //创建时间
        dataOffset += VECTOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,
            particleProgram.aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE)
    }

    /**
     * 绘制
     */
    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount)
    }
}