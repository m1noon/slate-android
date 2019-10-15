package github.m1noon.slateandroid.utils

import github.m1noon.slateandroid.operations.Operation
import kotlin.math.min

/**
 * Compare paths to see which is before or after.
 */
fun List<Int>.comparePath(target: List<Int>): Int? {
    val minSize = min(this.size, target.size)
    if (minSize > 0) {
        for (i in 0..minSize - 1) {
            val pv = this.get(i)
            val tv = target.get(i)
            if (pv < tv) return -1
            if (pv > tv) return 1
        }
    }

    return if (this.size == target.size) 0 else null
}

/**
 * Crop paths to an equal size with target, defaulting to the shortest.
 */
fun List<Int>.cropPath(
    target: List<Int>, size: Int = min(this.size, target.size)
): Pair<List<Int>, List<Int>> {
    val croppedThis = if (this.size >= size) this.subList(0, size) else listOf()
    val croppedTarget = if (target.size >= size) target.subList(0, size) else listOf()
    return Pair(croppedThis, croppedTarget)
}

/**
 * Get all ancestor paths of th this path.
 */
fun List<Int>.getAncestorPaths(): List<List<Int>> {
    val ancestors = mutableListOf<List<Int>>()
    for (i in this.indices) {
        ancestors.add(this.subList(0, i))
    }
    return ancestors.toList()
}

fun List<Int>.getAncestorPathsWithMe(): List<List<Int>> {
    return getAncestorPaths().plus(listOf(this))
}

/**
 * Decrement this path by [n] at [index], defaulting to the last index.
 */
fun List<Int>.decrementPath(n: Int = 1, index: Int = this.size - 1): List<Int> {
    return incrementPath(-n, index)
}

/**
 * Increment this path by [n] at [index], defaulting to the last index.
 */
fun List<Int>.incrementPath(n: Int = 1, index: Int = this.size - 1): List<Int> {
    val newValue = get(index) + n
    return mapIndexed { i, v -> if (i == index) newValue else v }
}

/**
 * Is this path above another [target] path?
 */
fun List<Int>.isAbovePath(target: List<Int>): Boolean {
    val cropped = cropPath(target)
    return this.size < target.size && cropped.first.comparePath(cropped.second) == 0
}

/**
 * Is this path after another [target] path in a document?
 */
fun List<Int>.isAfterPath(target: List<Int>): Boolean {
    val cropped = cropPath(target)
    return cropped.first.comparePath(cropped.second) == 1
}

/**
 * Is this path before another [target] path in a document?
 */
fun List<Int>.isBeforePath(target: List<Int>): Boolean {
    val cropped = cropPath(target)
    return cropped.first.comparePath(cropped.second) == -1
}

/**
 * Is this path equal to another [target] path in a document?
 */
fun List<Int>.isEqual(target: List<Int>): Boolean {
    return this == target
}

/**
 * Is this path older than a [target] path? Meaning that it ends as an older
 * sibling of one of the indexes in the target.
 */
fun List<Int>.isOlder(target: List<Int>): Boolean {
    val index = size - 1
    val cropped = cropPath(target, index)
    val pl = get(index)
    val tl = target.get(index)
    return cropped.first.isEqual(cropped.second) && pl > tl
}

/**
 * Is this path younger than a [target] path? Meaning that it ends as a younger
 * sibling of one of the indexes in the target.
 */
fun List<Int>.isYounger(target: List<Int>): Boolean {
    val index = this.size - 1
    val cropped = this.cropPath(target, index)
    val pl = getOrElse(index) { 0 }
    val tl = target.getOrElse(index) { 0 }
    return cropped.first == cropped.second && pl < tl
}

/**
 * Lift a `path` to refer to its `n`th ancestor.
 */
fun List<Int>.lift(n: Int = 1): List<Int> {
    if (size <= n) {
        return listOf()
    }
    val ancestor = subList(0, size - n)
    return ancestor
}

/**
 * Get the minimum length of paths this and [target].
 */
fun List<Int>.min(target: List<Int>): Int {
    return min(this.size, target.size)
}


fun List<Int>.transform(operation: Operation): List<List<Int>> {
    if (this.isEmpty()) {
        return listOf(this)
    }
    return when (operation) {
        is Operation.InsertText -> listOf(this)
        is Operation.InsertNode -> {
            if (operation.path.isAbovePath(this)) {
                listOf(this.incrementPath(1, operation.path.size - 1))
            } else {
                listOf(this)
            }
        }
        // TODO other operation
        else -> listOf(this)
    }
}