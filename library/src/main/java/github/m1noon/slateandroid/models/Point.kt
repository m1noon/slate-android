package github.m1noon.slateandroid.models

data class Point(
    val key: String = "",
    val offset: Int? = null,
    val path: List<Int> = listOf()
) {
    fun isSet(): Boolean {
        return this.key != "" && this.offset != null
    }

    fun isUnset(): Boolean {
        return !isSet()
    }

    fun setOffset(offset: Int?): Point {
        return copy(offset = offset)
    }

    fun moveForward(n: Int = 1): Point {
        if (n == 0) return this
        if (n < 0) return moveBackward(-n)

        return setOffset(offset ?: 0 + n)
    }

    fun moveBackward(n: Int = 1): Point {
        if (n == 0) return this
        if (n < 0) return moveForward(-n)
        return setOffset(offset ?: 0 - n)
    }
}