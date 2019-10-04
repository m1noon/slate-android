package github.m1noon.slateandroid.models

import java.lang.RuntimeException

data class Selection(
    override val anchor: Point = Point(),
    override val focus: Point = Point(),
    val isFocused: Boolean = false,
    val marks: List<Mark> = listOf()
) : Rangeable {

    @Suppress("UNCHECKED_CAST")
    override fun <R : Rangeable> setAnchor(anchor: Point): R {
        return try {
            copy(anchor = anchor) as R
        } catch (e: Throwable) {
            throw RuntimeException("Return type should be Selection: '${e}'")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R : Rangeable> setFocus(focus: Point): R {
        return try {
            copy(focus = focus) as R
        } catch (e: Throwable) {
            throw RuntimeException("Return type should be Selection: '${e}'")
        }
    }
}
