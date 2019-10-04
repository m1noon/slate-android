package github.m1noon.slateandroid.models

import github.m1noon.slateandroid.utils.isAfterPath
import github.m1noon.slateandroid.utils.isBeforePath

/**
 * The interface that `Decoration`, `Range` and `Selection` all implement, to make
 * working anchor and focus points easier.
 * refer to https://github.com/ianstormtaylor/slate/blob/master/packages/slate/src/interfaces/range.js
 */
interface Rangeable {
    val anchor: Point
    val focus: Point

    fun <R : Rangeable> setAnchor(anchor: Point): R
    fun <R : Rangeable> setFocus(focus: Point): R

    /**
     * Check whether the range is collapsed.
     */
    fun isCollapsed(): Boolean {
        return this.anchor == this.focus ||
                (this.anchor.key == this.focus.key && this.anchor.offset == this.focus.offset)
    }

    /**
     * Check whether the range is expanded.
     */
    fun isExpanded(): Boolean {
        return !isCollapsed()
    }

    /**
     * Check whether the range is backward.
     */
    fun isBackward(): Boolean {
        if (isUnset()) {
            return false
        }

        if (anchor.key == focus.key) {
            return anchor.offset!! > focus.offset!!
        }

        return focus.path.isBeforePath(anchor.path)
    }

    /**
     * Check whether the range is forward.
     */
    fun isForward(): Boolean {
        if (isUnset()) {
            return false
        }

        if (anchor.key == focus.key) {
            return anchor.offset!! > focus.offset!!
        }

        return focus.path.isAfterPath(anchor.path)
    }

    /**
     * Check whether the range isn't set.
     */
    fun isUnset(): Boolean {
        return anchor.isUnset() || focus.isUnset()
    }

    /**
     * Check whether the range is set.
     */
    fun isSet(): Boolean {
        return !this.isUnset()
    }

    /**
     * Get the start point.
     */
    fun start(): Point {
        return if (isBackward()) this.focus else this.anchor
    }

    /**
     * Get the end point.
     */
    fun end(): Point {
        return if (isBackward()) this.anchor else this.focus
    }

    // Setter

    fun <R : Rangeable> setStart(p: Point): R {
        return if (isBackward()) setFocus(p) else setAnchor(p)
    }

    fun <R : Rangeable> setEnd(p: Point): R {
        return if (isBackward()) setAnchor(p) else setFocus(p)
    }

    fun <R : Rangeable> setPoints(anchor: Point, focus: Point): R {
        return (setAnchor(anchor) as R).setFocus(focus)
    }

    fun <R : Rangeable> updatePoints(updater: (Point) -> Point): R {
        return setPoints(updater(anchor), updater(focus))
    }
}
