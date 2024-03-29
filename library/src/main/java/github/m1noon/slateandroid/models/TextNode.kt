package github.m1noon.slateandroid.models

import java.util.*

data class TextNode(
    override val objectType: ObjectType = ObjectType.Text,
    override val key: String,
    override val text: String = "",
    override val marks: List<Mark> = listOf()
) : Node {
    override val type: Nothing? = null
    override val nodes: Nothing? = null
    override val data: Nothing? = null

    constructor(n: Node) : this(n.objectType, n.key, n.text ?: "", n.marks ?: listOf())

    override fun updateKey(key: String): Node {
        return copy(key = key)
    }

    override fun updateNodes(nodes: List<Node>): Node {
        return this
    }

    override fun mergeProperties(property: NodeProperty): Node {
        return copy(
            text = property.text ?: this.text,
            marks = property.marks ?: this.marks
        )
    }

    /**
     * Add a [mark].
     */
    fun addMark(mark: Mark): TextNode {
        return copy(
            marks = marks.plus(mark)
        )
    }

    /**
     * Add a set of [marks].
     */
    fun addMarks(marks: List<Mark>): TextNode {
        return copy(
            marks = marks.plus(marks)
        )
    }

    fun insertText(index: Int, text: String): TextNode {
        return copy(
            text = "${this.text.substring(0, index)}${text}${this.text.substring(
                index,
                this.text.length
            )}"
        )
    }

    /**
     * Remove a [mark].
     */
    fun removeMark(mark: Mark): TextNode {
        return copy(
            marks = marks.filter { it.type != mark.type }
        )
    }

    fun removeText(index: Int, length: Int): TextNode {
        return copy(
            text = this.text.substring(0, index) + this.text.substring(index + length)
        )
    }

    /**
     * Split the node into two at `index`.
     */
    fun splitText(index: Int): List<TextNode> {
        val one = copy(text = text.substring(0, index))
        val two = copy(text = text.substring(index), key = UUID.randomUUID().toString())
        return listOf(one, two)
    }
}
