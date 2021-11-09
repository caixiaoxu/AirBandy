package com.lsy.airhockey.shape

import kotlin.math.sqrt

class Geometry {
    class Point(val x: Float, val y: Float, val z: Float) {
        /**
         * 向Y平移
         * @param distance 距离
         */
        fun translatY(distance: Float): Point = Point(x, y + distance, z)

        /**
         * 按向量平移
         * @param vector 向量
         */
        fun translate(vector: Vector): Point = Point(x + vector.x, y + vector.y, z + vector.z)
    }

    class Circle(val center: Point, val radius: Float) {
        /**
         * 按半径缩放
         * @param scale 缩放度
         */
        fun scale(scale: Float): Circle = Circle(center, radius * scale)
    }

    class Cylinder(val center: Point, val radius: Float, val height: Float) {

    }

    class Ray(val point: Point, val vector: Vector) {
    }

    class Vector(val x: Float, val y: Float, val z: Float) {
        /**
         * 向量的长度
         */
        fun length(): Float = sqrt(x * x + y * y + z * z)

        /**
         * 计算两个向量相乘
         * @param other 另一个向量
         */
        fun crossProduct(other: Vector): Vector =
            Vector((y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x))

        /**
         * 计算两个向量的点积
         * @param other 另一个向量
         */
        fun dotProduct(other: Vector): Float = x * other.x + y * other.y + z * other.z

        /**
         * 缩放
         * @param f 缩放量
         */
        fun scale(f: Float): Vector = Vector(x * f, y * f, z * f)

        fun normalize(): Vector = scale(1f / length())

    }

    class Sphere(val center: Point, val radius: Float) {}

    class Plane(val point: Point, val normal: Vector) {}

    companion object {
        /**
         * 计算两个点之间的距离向量
         */
        @JvmStatic
        fun vectorBetween(from: Point, to: Point) =
            Vector(to.x - from.x, to.y - from.y, to.z - from.z)

        /**
         * 计算物体和射线是否相交
         * 射线与中心点连线计算高，与半径做判断
         * @param sphere 物体(球)
         * @param ray 射线
         */
        @JvmStatic
        fun intersects(sphere: Sphere, ray: Ray): Boolean =
            distanceBetween(sphere.center, ray) < sphere.radius

        /**
         * 计算两个点之间的距离
         * @param point 中心
         * @param ray 射线
         */
        @JvmStatic
        fun distanceBetween(point: Point, ray: Ray): Float {
            //中心点到射线两端的两个向量
            val p1ToPoint = vectorBetween(ray.point, point)
            val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)
            //通过两个向量相乘获得面积，刚好的三角形的2倍
            val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
            //射线的长度
            val lengthOfBase = ray.vector.length()
            //求高
            return areaOfTriangleTimesTwo / lengthOfBase
        }

        /**
         * 相交的点
         * @param ray 射线
         */
        @JvmStatic
        fun intersectionPoint(ray: Ray, plane: Plane): Point {
            val rayToPlaneVector = vectorBetween(ray.point, plane.point)
            //缩放因子
            val scaleFactor =
                rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal)
            return ray.point.translate(ray.vector.scale(scaleFactor))
        }
    }
}