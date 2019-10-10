package github.m1noon.slateandroid.models

import android.util.Log
import kotlin.math.min

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

    /**
     * Normalize the point relative to a [node], ensuring that its key and path
     * reference a text node, or that it gets unset.
     */
    fun normalize(node: Node): Point {
        // PERF: this function gets called a lot.
        // to avoid creating the key -> path lookup table, we attempt to look up by path first.
        var target = node.getNodeByPath(this.path)
        if (target == null) {
            target = node.getNodeByKey(this.key)
            if (target != null) {
                return this.copy(
                    path = node.getPathByKey(this.key)
                )
            }
        }

        if (target == null) {
            Log.w("Point", "A point's `path` or `key` invalid and was reset!")
            val textNode = node.getFirstText() ?: return Point()

            return this.copy(
                key = textNode.key,
                offset = 0,
                path = node.getPathByKey(textNode.key)
            )
        }

        if (target.objectType != ObjectType.Text) {
            Log.w("Point", "A point should not reference a non-text node!")
            val text = target.getTextAtOffset(this.offset ?: 0) ?: return Point()
            val before = target.getOffsetByKey(text.key)
            return this.copy(
                offset = (this.offset ?: 0) - before,
                key = text.key,
                path = node.getPathByKey(text.key)
            )
        }

        if (this.key.isNotEmpty() && this.key != target.key) {
            Log.w("Point", "A point's [key] did not match its [path]!")
        }

        val point = this.copy(
            key = target.key,
            path = if (this.path.isEmpty()) node.getPathByKey(target.key) else this.path,
            offset = min(offset ?: 0, target.text.orEmpty().length)
        )

        return point
    }
}