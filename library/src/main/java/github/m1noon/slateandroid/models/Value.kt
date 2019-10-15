package github.m1noon.slateandroid.models

import android.util.Log
import github.m1noon.slateandroid.utils.incrementPath
import java.util.*

data class Value(
    val objectType: ObjectType = ObjectType.Value,
    val document: Document = Document(),
    val selection: Selection = Selection(),
    val data: Data? = null
) {

    companion object {
        val Default: Value = Value(
            document = Document(
                key = UUID.randomUUID().toString(),
                nodes = listOf(
//                    BlockNode(
//                        type = BlockNodeType.HEADING_1,
//                        key = UUID.randomUUID().toString(),
//                        nodes = listOf(
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = "This is"
//                            ),
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = "sample",
//                                marks = listOf(Mark(type = MarkType.BOLD))
//                            ),
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = " text"
//                            )
//                        )
//                    ),
//                    BlockNode(
//                        type = BlockNodeType.PARAGRAPH,
//                        key = UUID.randomUUID().toString(),
//                        nodes = listOf(
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = "This is "
//                            ),
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = "sample",
//                                marks = listOf(Mark(type = MarkType.BOLD))
//                            ),
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = " text"
//                            )
//                        ),
//                        data = mapOf()
//                    ),
                    BlockNode(
                        type = BlockNodeType.PARAGRAPH,
                        key = UUID.randomUUID().toString(),
                        nodes = listOf(
//                            TextNode(
//                                key = UUID.randomUUID().toString(),
//                                text = "This is 'first' text of 2nd paragraph."
//                            ),
//                            BlockNode(
//                                type = BlockNodeType.PARAGRAPH,
//                                key = UUID.randomUUID().toString(),
//                                nodes = listOf(
//                                    TextNode(
//                                        key = UUID.randomUUID().toString(),
//                                        text = "This is "
//                                    ),
//                                    TextNode(
//                                        key = UUID.randomUUID().toString(),
//                                        text = "'second'",
//                                        marks = listOf(Mark(type = MarkType.ITALIC))
//                                    ),
//                                    TextNode(
//                                        key = UUID.randomUUID().toString(),
//                                        text = " text of 2nd paragraph."
//                                    )
//                                )
//                            ),
                            TextNode(
                                key = UUID.randomUUID().toString(),
                                text = "123"
                            ),
                            TextNode(
                                key = UUID.randomUUID().toString(),
                                text = "456",
                                marks = listOf(Mark(type = MarkType.BOLD))
                            ),
                            TextNode(
                                key = UUID.randomUUID().toString(),
                                text = "789"
                            )
                        )
                    )
                )
            )
        )
    }

    /**
     * Get the current start text node's closest block parent.
     */
    fun startBlock(): Node? {
        return selection.start().key.let { key ->
            if (key == "") null else document.getClosestBlockByKey(key)
        }
    }

    fun activeMarks(): Set<Mark> {
        return if (selection.isUnset()) {
            setOf()
        } else if (selection.marks.isNotEmpty()) {
            return selection.marks.toSet()
        } else {
            return document.getActiveMarksAtRange(selection)
        }
    }

    fun addMark(path: List<Int>, mark: Mark): Value {
        return copy(
            document = document.addMark(path, mark) as Document
        )
    }

    fun insertNode(path: List<Int>, node: Node): Value {
        return copy(
            document = document.insertNode(path, node) as Document
        ).let { v ->
            v.document.getNextText(path)?.let { tn ->
                v.mapPoints {
                    Point(
                        key = tn.key,
                        path = v.document.getPathByKey(tn.key),
                        offset = 0
                    )
                }
            } ?: v
        }
    }

    fun insertText(path: List<Int>, offset: Int, text: String): Value {
        return copy(
            document = document.insertText(path, offset, text) as Document
        )
    }

    /**
     * Remove [mark] at [path].
     */
    fun removeMark(path: List<Int>, mark: Mark): Value {
        return copy(
            document = document.removeMark(path, mark) as Document
        )
    }

    /**
     * Remove a node by [path].
     */
    fun removeNode(path: List<Int>): Value {
        // find previous text node to set selection to.
        val prevTextNode = document.getPreviousText(path)

        return copy(
            document = document.removeNode(path) as Document
        ).let { v ->
            v.mapPoints {
                if (prevTextNode != null) {
                    Point(
                        key = prevTextNode.key,
                        path = v.document.getPathByKey(prevTextNode.key),
                        offset = prevTextNode.text?.length ?: 0
                    )
                } else Point()
            }
        }
    }

    /**
     * Remove [text] at [offset] in node by [path].
     */
    fun removeText(path: List<Int>, offset: Int, text: String): Value {
        val value = copy(
            document = document.removeText(path, offset, text) as Document
        )

        // FIXME map points?

        return value
    }

    /**
     * Replace text of [length] at [offset] to new [text] in node by [path]
     * (*) This is original function.
     */
    fun replaceText(
        path: List<Int>,
        offset: Int,
        length: Int,
        text: String,
        marks: List<Mark>? = null
    ): Value {
        val value = copy(
            document = document.removeTextWithLength(path, offset, length).insertText(
                path,
                offset,
                text
            ) as Document
        )

        // FIXME map points?

        return value
    }

    /**
     * Set [property] on a node.
     */
    fun setNode(path: List<Int>, property: NodeProperty): Value {
        return copy(
            document = document.setNode(path, property) as Document
        )
    }

    /**
     * Set [selection] on the selection.
     */
    fun setSelection(selection: Selection): Value {
        return copy(selection = document.resolveSelection(selection))
    }

    fun splitNode(path: List<Int>, position: Int, property: NodeProperty?): Value {
        return copy(
            document = document.splitNode(path, position, property) as Document
        ).let { v ->
            // set point to the first text node of newly created path which is the next sibling block of the block specified path as first argument.
            v.document.getNextText(path.incrementPath())?.let { tn ->
                v.mapPoints {
                    Point(
                        key = tn.key,
                        path = v.document.getPathByKey(tn.key),
                        offset = 0
                    )
                }
            } ?: v
        }
    }

    fun mergeNode(from: List<Int>, to: List<Int>): Value {

        // find last text node in 'to' block to which set selection. FIXME
        val lastTextNodeOfTo = document.getNodeByPath(to)?.getLastText()

        return copy(
            document = document.mergeNodes(from, to) as Document
        ).let { v ->
            lastTextNodeOfTo?.let { tn ->
                v.mapPoints {
                    Point(
                        key = tn.key,
                        path = v.document.getPathByKey(tn.key),
                        offset = tn.text.length
                    )
                }
            } ?: v
        }
    }

    /**
     * Move a node by [path] to [newPath].
     *
     * A [newIndex] can be provided when move nodes by [key], to account for not
     * being able to have a key for a location in the tree that doesn't exist yet.
     */
    fun moveNode(path: List<Int>, newPath: List<Int>, newIndex: Int = 0): Value {
        if (path == newPath) {
            return this
        }

        // TODO map points
        return copy(
            document = document.moveNode(path, newPath, newIndex) as Document
        ).let { v ->
            v.mapPoints { it.copy(path = v.document.getPathByKey(it.key)) }
        }
    }

    /**
     * Map all range objects to apply adjustments with an `iterator`.
     */
    fun mapRanges(updater: (Rangeable) -> Rangeable): Value {
        return copy(
            selection = updater(selection) as Selection
            // TODO annotation
        )
    }

    fun mapPoints(updater: (Point) -> Point): Value {
        return mapRanges { it.updatePoints(updater) }
    }

    fun moveForward(n: Int = 1): Value {
        if (n == 0) {
            return this
        }

        return copy(
            selection = selection.setPoints(
                anchor = movePointForward(selection.anchor, n),
                focus = movePointForward(selection.focus, n)
            )
        )
    }

    fun movePointForward(p: Point, n: Int = 1): Point {
        var n = n
        for (tn in document.texts(p.path)) {
            val offset = if (tn.key == p.key) p.offset ?: 0 else 0
            val length = tn.text.orEmpty().length - offset
            if (n <= length) {
                return Point(key = tn.key, path = document.getPathByKey(tn.key), offset = n)
            }
            n -= 1
        }
        return document.getLastText()?.let {
            Point(
                key = it.key,
                path = document.getPathByKey(it.key),
                offset = it.text.length
            )
        } ?: p
    }
}
