package github.m1noon.slateandroid.models

import github.m1noon.slateandroid.utils.decrementPath
import github.m1noon.slateandroid.utils.equalsIgnoringText
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.lift
import java.lang.AssertionError
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.util.*

/**
 * The interface that [Document], [BlockNode] and [InlineNode] all implement, to make
 * working with the recursive node tree easier.
 *
 * https://github.com/ianstormtaylor/slate/blob/master/packages/slate/src/interfaces/node.js
 * https://github.com/ianstormtaylor/slate/blob/master/packages/slate/src/interfaces/element.js
 */
interface Node {
    val objectType: ObjectType
    val key: String
    // for [BlockNode], [InlineNode], and [Document]
    val type: Type?
    val nodes: List<Node>?
    val data: Data?
    // for [TextNode]
    val text: String?
    val marks: List<Mark>?

    interface Type

    fun updateKey(key: String): Node
    fun updateNodes(nodes: List<Node>): Node

    enum class Direction {
        FORWARD,
        BACKWARD,
        NONE
    }

    fun getFirstText(): TextNode? {
        if (this.objectType == ObjectType.Text) {
            return this.toTextNode()
        }
        return nodes?.let {
            for (node in it) {
                if (node.objectType == ObjectType.Text) {
                    return node.toTextNode()
                }
                val tn = node.getFirstText()
                if (tn != null) {
                    return tn
                }
            }
            return null
        }
    }

    fun getLastText(): TextNode? {
        if (this.objectType == ObjectType.Text) {
            return this.toTextNode()
        }
        return nodes?.let {
            val reversed = it.reversed()
            for (node in reversed) {
                if (node.objectType == ObjectType.Text) {
                    return node.toTextNode()
                }
                val tn = node.getLastText()
                if (tn != null) {
                    return tn
                }
            }
            return null
        }
    }

    fun getNodeByKey(key: String): Node? {
        if (this.key == key) {
            return this
        }
        return getNodeByPath(getPathByKey(key))
    }

    fun getNodeByPath(path: List<Int>): Node? {
        if (this.objectType == ObjectType.Text && path.isNotEmpty()) {
            return null
        }
        return if (path.isNotEmpty()) {
            return getDescendantByPath(path)
        } else this
    }

    fun assertNodeByKey(key: String): Node {
        return getNodeByKey(key) ?: throw AssertionError("node not found: {key='${key}'}")
    }

    fun assertNodeByPath(path: List<Int>): Node {
        return getNodeByPath(path) ?: throw AssertionError("node not found: {path='${path}'}")
    }

    fun getKeysToPathTable(): Map<String, List<Int>> {
        val m = mutableMapOf<String, List<Int>>(key to listOf())

        nodes?.forEachIndexed { i, node ->
            val nested = node.getKeysToPathTable()

            nested.keys.forEach { key ->
                val path = nested[key] ?: listOf()
                m.put(key, listOf(i).plus(path))
            }
        }
        return m.toMap()
    }

    fun getPathByKey(key: String): List<Int> {
        val table = getKeysToPathTable()
        return table[key] ?: listOf()
    }

    fun getTextString(): String {
        if (this.objectType == ObjectType.Text) {
            return this.text ?: ""
        }
        return this.nodes?.fold("") { acc: String, node: Node -> acc + node.getTextString() } ?: ""
    }

    /**
     * Regenerate the node's key.
     */
    fun regenerateKey(): Node {
        return updateKey(UUID.randomUUID().toString())
    }

    private fun toTextNode(): TextNode {
        return when (this) {
            is TextNode -> this
            else -> {
                if (this.objectType == ObjectType.Text) {
                    return TextNode(this)
                } else throw RuntimeException("this node is not text node: {objectType='${objectType}'}")
            }
        }
    }


    // https://github.com/ianstormtaylor/slate/blob/master/packages/slate/src/interfaces/element.js


    fun addMark(path: List<Int>, mark: Mark): Node {
        val node = assertDescendantByPath(path)
        if (node is TextNode) {
            return replaceNode(path, node.addMark(mark))
        }
        return this
    }

    /**
     * Create an [Iterable] for all of the ancestors of the [path].
     */
    fun ancestors(path: List<Int>): Iterable<Node> {
        return createIterable(
            targetPath = path,
            direction = Direction.NONE,
            downward = false,
            includeTargetAncestors = true,
            includeRoot = true
        )
    }

    fun blocks(
        targetPath: List<Int> = listOf(),
        direction: Direction = Direction.FORWARD,
        downward: Boolean = true,
        upward: Boolean = true,
        includeRoot: Boolean = false,
        includeTarget: Boolean = false,
        includeTargetAncestors: Boolean = false,
        onlyLeaves: Boolean = false,
        onlyTypes: Set<Type> = setOf()
    ): Iterable<Node> {
        return createIterable(
            includeDocument = false,
            includeInlines = false,
            includeText = false,
            targetPath = targetPath,
            direction = direction,
            downward = downward,
            upward = upward,
            includeRoot = includeRoot,
            includeTarget = includeTarget,
            includeTargetAncestors = includeTargetAncestors
        ) { node, path ->
            if (onlyLeaves && !node.isLeafBlock()) {
                false
            } else if (onlyTypes.isNotEmpty() && !onlyTypes.contains(node.type)) {
                false
            } else {
                true
            }
        }
    }

    /**
     * Create an iteratable function starting at [target] path with `options`.
     */
    fun createIterable(
        targetPath: List<Int> = listOf(),
        direction: Direction = Direction.FORWARD,
        downward: Boolean = true,
        upward: Boolean = true,
        includeRoot: Boolean = false,
        includeDocument: Boolean = true,
        includeBlocks: Boolean = true,
        includeInlines: Boolean = true,
        includeTarget: Boolean = false,
        includeTargetAncestors: Boolean = false,
        includeText: Boolean = true,
        range: Rangeable? = null,
        match: ((node: Node, path: List<Int>) -> Boolean)? = null
    ): Iterable<Node> {
        val root: Node = this
        val targetNode = getNodeByPath(targetPath)
        var targetRange: Rangeable? = range
        var path: List<Int>? = targetRange?.start()?.path ?: targetPath
        var node: Node? = path?.let { getNodeByPath(it) }
        val visited: MutableSet<Node> = mutableSetOf()
        val endPath: List<Int>? = targetRange?.end()?.path
        var isEndRange: Boolean = false

        return object : Iterable<Node> {
            override fun iterator(): Iterator<Node> {
                return object : Iterator<Node> {

                    init {
                        path?.let { p ->
                            node?.let { n ->
                                val nextPair = result(p, n)
                                path = nextPair?.first
                                node = nextPair?.second
                            }
                        }
                    }

                    override fun hasNext(): Boolean {
                        return path != null && node != null
                    }

                    override fun next(): Node {
                        // save path & node to return
                        val retPath = path ?: throw IllegalStateException("")
                        val retNode = node ?: throw IllegalStateException("")

                        // calc next path & node for next iteration
                        val nextPair = calcNext(retPath, retNode)
                        path = nextPair?.first
                        node = nextPair?.second

                        return retNode
                    }

                    private fun calcNext(
                        currentPath: List<Int>,
                        currentNode: Node
                    ): Pair<List<Int>, Node>? {
                        // If children nodes exists, move to the top of the child node. (Except when children has been scanned once)
                        // If we're allowed to go downward, and we haven't decsended yet, do so.
                        if (
                            downward
                            && currentNode.nodes.orEmpty().isNotEmpty()
                            && !visited.contains(currentNode)
                        ) {
                            visited.add(currentNode)
                            val nextIndex =
                                if (direction == Direction.FORWARD) 0 else currentNode.nodes!!.size - 1
                            val newPath = currentPath.plus(nextIndex)
                            val newNode = root.getNodeByPath(newPath)!!
                            return result(newPath, newNode)
                        }

                        // Increase the last value of the path by 1, to move to the younger brother node
                        // If we're going forward...
                        if (direction == Direction.FORWARD && currentPath.isNotEmpty()) {
                            val newPath = currentPath.incrementPath()
                            val newNode = root.getNodeByPath(newPath)
                            if (newNode != null) {
                                return result(newPath, newNode)
                            }
                        }

                        // Decrease the last value of the path by 1, to move to the older brother node
                        // If we're going backward...
                        if (direction == Direction.BACKWARD && currentPath.isNotEmpty() && currentPath.last() != 0) {
                            val newPath = currentPath.decrementPath()
                            val newNode = root.getNodeByPath(newPath)
                            if (newNode != null) {
                                return result(newPath, newNode)
                            }
                        }

                        // If we're going upward...
                        if (upward && currentPath.isNotEmpty()) {
                            val newPath = currentPath.lift()
                            val newNode = root.getNodeByPath(newPath)!!

                            // Sometimes we'll have already visited the node on the way down
                            // so we don't want to double count it.
                            if (visited.contains(newNode)) {
                                return calcNext(newPath, newNode)
                            }

                            visited.add(newNode)

                            // If ancestors of the target node shouldn't be included, skip them.
                            if (!includeTargetAncestors) {
                                return calcNext(newPath, newNode)
                            } else {
                                return result(newPath, newNode)
                            }
                        }

                        return null
                    }

                    private fun result(path: List<Int>, node: Node): Pair<List<Int>, Node>? {
                        if (!includeTarget && targetPath.equals(path)) {
                            return calcNext(path, node)
                        }

                        if (isEndRange) {
                            return null
                        }
                        if (endPath != null && path == endPath) {
                            isEndRange = true
                        }

                        // skip document
                        if (!includeDocument && node.objectType == ObjectType.Document) {
                            return calcNext(path, node)
                        }
                        // skip block
                        if (!includeBlocks && node.objectType == ObjectType.Block) {
                            return calcNext(path, node)
                        }
                        // skip inline
                        if (!includeInlines && node.objectType == ObjectType.Inline) {
                            return calcNext(path, node)
                        }
                        // skip text
                        if (!includeText && node.objectType == ObjectType.Text) {
                            return calcNext(path, node)
                        }

                        if (match != null && !match(node, path)) {
                            return calcNext(path, node)
                        }

                        return Pair(path, node)
                    }
                }
            }
        }
    }

    /**
     * Create an iteratable for all of the descendants of the node.
     */
    fun descendants(
        targetPath: List<Int> = listOf(),
        downward: Boolean = true,
        upward: Boolean = true,
        includeRoot: Boolean = false,
        includeDocument: Boolean = true,
        includeBlocks: Boolean = true,
        includeInlines: Boolean = true,
        includeTarget: Boolean = false,
        includeText: Boolean = true,
        range: Rangeable? = null,
        direction: Direction = Direction.FORWARD
    ): Iterable<Node> {
        return this.createIterable(
            targetPath = targetPath,
            downward = downward,
            upward = upward,
            includeRoot = includeRoot,
            includeDocument = includeDocument,
            includeBlocks = includeBlocks,
            includeInlines = includeInlines,
            includeTarget = includeTarget,
            includeText = includeText,
            range = range,
            direction = direction
        )
    }

    /**
     * Get a set of the active marks in a `range`. Active marks are marks that are
     * on every text node in a given range. This is a common distinction for
     * highlighting toolbar buttons for example.
     *
     * TODO: this method needs to be cleaned up, it's very hard to follow and probably doing unnecessary work.
     */
    fun getActiveMarksAtRange(range: Rangeable): Set<Mark> {
        // FIXME is it too simple? see 'slate-js' source code which has many operation.
        val texts = getTextsAtRange(range)
        return texts
            .filter { tn ->
                var include = true
                if (tn.key == range.start().key) {
                    include = range.start().offset ?: 0 < tn.text.orEmpty().length
                }
                if (tn.key == range.end().key) {
                    include = range.end().offset ?: 0 > 0
                }
                include
            }
            .foldIndexed(setOf()) { index, acc, node ->
                if (index == 0) {
                    node.marks.orEmpty().toSet()
                } else {
                    acc.intersect(node.marks.orEmpty())
                }
            }
    }

    fun getClosestByKey(key: String, predicate: (Node) -> Boolean): Node? {
        return getClosestByPath(getPathByKey(key), predicate)
    }

    /**
     * Get closest parent of node that matches a [predicate].
     */
    fun getClosestByPath(path: List<Int>, predicate: (Node) -> Boolean): Node? {
        val iter = ancestors(path)
        for (n in iter) {
            if (predicate(n)) {
                return n
            }
        }
        return null
    }

    /**
     * Get the closest block parent of a node by [path].
     */
    fun getClosestBlock(path: List<Int>): Node? {
        return getClosestByPath(path) { it.objectType == ObjectType.Block }
    }

    fun getClosestBlockByKey(key: String): Node? {
        return getClosestBlock(getPathByKey(key))
    }

    /**
     * Get the closest inline parent of a node by [path].
     */
    fun getClosestInline(path: List<Int>): Node? {
        return getClosestByPath(path) { it.objectType == ObjectType.Inline }
    }

    /**
     * Get a descendant node by [key].
     */
    fun getDescendantByKey(key: String): Node? {
        val path = this.getPathByKey(key)
        return getDescendantByPath(path)
    }

    /**
     * Get a descendant node by [path].
     */
    fun getDescendantByPath(path: List<Int>): Node? {
        if (path.isEmpty()) {
            return null
        }

        var node: Node? = this

        path.forEach { index ->
            node = node?.nodes?.getOrNull(index)
        }

        return node
    }

    fun assertDescendantByPath(path: List<Int>): Node {
        return getDescendantByPath(path)
            ?: throw AssertionError("descendant node not found in path : {path='${path}'}")
    }

    /**
     * Get a set of marks that would occur on the next insert at a [point] in the
     * node. This mimics expected rich text editing behaviors of mark contiuation.
     */
    fun getInsertMarksAtPoint(point: Point): Set<Mark> {
        // TODO resolve point

        val offset = point.offset ?: 0
        val textNode = getDescendantByPath(point.path)

        // PERF: we can exit early if the offset isn't at the start of the node.
        if (offset > 0) {
            return textNode?.marks.orEmpty().toSet()
        }

        var blockNode: Node? = null
        var blockPath: List<Int>? = null

        for (node in ancestors(point.path)) {
            if (node.objectType == ObjectType.Block) {
                blockNode = node
            }
        }
        blockNode?.let { blockPath = getPathByKey(it.key) }

        // get prevous text node
        val relativePath = point.path.drop(blockPath.orEmpty().size)
        val previous = blockNode?.texts(
            targetPath = relativePath,
            direction = Direction.BACKWARD
        )?.iterator()?.let {
            if (it.hasNext()) it.next() else null
        }

        // If there's no previous text, we're at the start of the block, so use
        // the current text nodes marks.
        if (previous == null) {
            return textNode?.marks.orEmpty().toSet()
        }

        // Otherwise, continue with the previous text node's marks instead.
        return previous.marks.orEmpty().toSet()
    }

    /**
     * Get a set of marks that would occur on the next insert at a [range].
     * This mimics expected rich text editing behaviors of mark contiuation.
     */
    fun getInsertMarksAtRange(range: Rangeable): Set<Mark> {
        // TODO resolve range

        if (range.isUnset()) {
            return setOf()
        }

        if (range.isCollapsed()) {
            return getInsertMarksAtPoint(range.start())
        }

        return getDescendantByPath(range.start().path)?.marks.orEmpty().toSet()
    }

    /**
     * Get the next sibling of a node by [path].
     */
    fun getNextSibling(path: List<Int>): Node? {
        val iter = siblings(path, Direction.FORWARD).iterator()
        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    fun getNextSiblingByKey(key: String): Node? {
        return getNextSibling(getPathByKey(key))
    }

    /**
     * Get the text node after a descendant text node by [path].
     */
    fun getNextText(path: List<Int>): Node? {
        val iter = createIterable(
            targetPath = path,
            includeTarget = false,
            includeDocument = false,
            includeBlocks = false,
            includeInlines = false
        ).iterator()

        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    /**
     * Get the offset for a descendant text node by [path].
     */
    fun getOffset(path: List<Int>): Int {
        assertDescendantByPath(path)
        val index = path.firstOrNull() ?: return 0

        val offset = this.nodes?.subList(0, index)?.fold(0) { acc, node ->
            acc + node.text.orEmpty().length
        } ?: 0

        val ret = if (path.size == 1) {
            offset
        } else {
            offset + (this.nodes?.getOrNull(index)?.getOffset(path.drop(1)) ?: 0)
        }

        return ret
    }

    fun getOffsetByKey(key: String): Int {
        return getOffset(getPathByKey(key))
    }

    /**
     * Get the parent of a descendant node at [path].
     */
    fun getParent(path: List<Int>): Node? {
        if (path.isEmpty()) {
            return null
        }
        val parentPath = path.lift()
        return getNodeByPath(parentPath)
    }

    fun getParentByKey(key: String): Node? {
        return getParent(getPathByKey(key))
    }

    /**
     * Get the previous sibling of a node by [path].
     */
    fun getPreviousSibling(path: List<Int>): Node? {
        val iter = siblings(path, Direction.BACKWARD).iterator()
        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    fun getPreviousSiblingByKey(key: String): Node? {
        return getPreviousSibling(getPathByKey(key))
    }

    /**
     * Get the text node before a descendant text node by [path].
     */
    fun getPreviousText(path: List<Int>): Node? {
        val iter = createIterable(
            targetPath = path,
            includeTarget = false,
            includeDocument = false,
            includeBlocks = false,
            includeInlines = false,
            direction = Direction.BACKWARD
        ).iterator()

        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    fun getPreviousTextByKey(key: String): Node? {
        return getPreviousText(getPathByKey(key))
    }

    /**
     * Get the descendant text node at an `offset`.
     */
    fun getTextAtOffset(offset: Int): Node? {
        // few shortcuts for the obvious cases.
        if (offset == 0) return getFirstText()
        if (offset == this.text?.length) return getLastText()
        if (offset < 0 || (this.text?.length ?: 0) < offset) return null

        var length = 0
        for (node in this.texts()) {
            length += node.text?.length ?: 0
            if (length > offset) {
                return node
            }
        }
        return null
    }

    /**
     * Recursively get all of the child text nodes in order of appearance.
     */
    fun getTexts(): List<Node> {
        val iterable = texts()
        return iterable.toList()
    }

    /**
     * Get all of the text nodes in a [range] as a List.
     */
    fun getTextsAtRange(range: Rangeable): List<Node> {
        val iterable = texts(range = range)
        return iterable.toList()
    }

    /**
     * Insert a [node].
     */
    fun insertNode(path: List<Int>, node: Node): Node {
        val index = path.last()
        val parentPath = path.lift()
        val parentNode = assertNodeByPath(parentPath)
        val updatedParentNode = parentNode.let {
            val before = it.nodes.orEmpty().subList(0, index)
            val after = it.nodes.orEmpty().subList(index, it.nodes.orEmpty().size)
            it.updateNodes(before.plus(node).plus(after))
        }
        return updatedParentNode.let { replaceNode(parentPath, it) } ?: this
    }

    fun insertNodeByKey(key: String, node: Node): Node {
        val path = getPathByKey(key)
        return insertNode(path, node)
    }

    fun insertText(path: List<Int>, offset: Int, text: String): Node {
        val node = getDescendantByPath(path)?.let {
            if (it is TextNode) it.insertText(
                offset,
                text
            ) else null
        }
        return node?.let { replaceNode(path, it) } ?: this
    }

    fun replaceNode(path: List<Int>, node: Node): Node {
        if (path.isEmpty()) return node

        return updateNodes(
            nodes?.mapIndexed { i, n ->
                if (i == path.first()) {
                    n.replaceNode(path.drop(1), node)
                } else n
            }.orEmpty()
        )
    }

    fun isLeafBlock(): Boolean {
        if (objectType != ObjectType.Block) {
            return false
        }
        return nodes?.firstOrNull { it.objectType == ObjectType.Block } == null
    }

    fun isLeafInline(): Boolean {
        if (objectType != ObjectType.Inline) {
            return false
        }
        return nodes?.firstOrNull { it.objectType == ObjectType.Inline } == null
    }

    fun removeMark(path: List<Int>, mark: Mark): Node {
        val node = assertDescendantByPath(path)
        return if (node is TextNode) {
            replaceNode(path, node.removeMark(mark))
        } else this
    }

    /**
     * Remove a node on [path].
     */
    fun removeNode(path: List<Int>): Node {
        if (path.isEmpty()) {
            return this
        }

        val i = path.first()
        if (path.size == 1) {
            return updateNodes(nodes.orEmpty().filterIndexed { index, _ -> i != index })
        } else {
            return updateNodes(nodes.orEmpty().mapIndexed { index, node ->
                if (index == i) {
                    node.removeNode(path.drop(1))
                } else node
            })
        }
    }

    /**
     * Remove [text] at [offset] in node on [path].
     */
    fun removeText(path: List<Int>, offset: Int, text: String): Node {
        return removeTextWithLength(path, offset, text.length)
    }

    fun removeTextWithLength(path: List<Int>, offset: Int, length: Int): Node {
        return getDescendantByPath(path)
            ?.let { TextNode(it) }
            ?.removeText(offset, length)
            ?.let { replaceNode(path, it) }
            ?: this
    }

    /**
     * Resolve a `selection`, relative to the node, ensuring that the keys and
     * offsets in the selection exist and that they are synced with the paths.
     */
    fun resolveSelection(selection: Selection): Selection {
        // FIXME avoid force cast
        return selection.normalize(this) as Selection
    }

    /**
     * Create an [Iterable] for the siblings in the tree at [path].
     */
    fun siblings(path: List<Int>, direction: Direction): Iterable<Node> {
        return createIterable(
            targetPath = path,
            upward = false,
            downward = false,
            direction = direction
        )
    }

    /**
     * Split a node by [path] at [position] with optional [properties] to apply
     * to the newly split node.
     */
    fun splitNode(path: List<Int>, position: Int, property: NodeProperty? = null): Node {
        val child = assertNodeByPath(path)

        val one: Node
        val two: Node
        if (child.objectType == ObjectType.Text) {
            val nodes = (child as TextNode).splitText(position)
            one = nodes[0]
            two = nodes[1]
        } else {
            val befores = child.nodes.orEmpty().subList(0, position)
            val afters = child.nodes.orEmpty().subList(position, child.nodes.orEmpty().size)
            one = child.updateNodes(befores)
            two = child.updateNodes(afters).regenerateKey()

            // TODO apply property
            if (property != null) {

            }
        }

        val ret = removeNode(path)
            .insertNode(path, two)
            .insertNode(path, one)
        return ret
    }

    /**
     * Create an iteratable for all the text node descendants.
     *
     * @param {Object} options
     * @return {Iterable}
     */
    fun texts(
        targetPath: List<Int> = listOf(),
        range: Rangeable? = null,
        direction: Direction = Direction.FORWARD
    ): Iterable<Node> {
        return this.descendants(
            targetPath = targetPath,
            includeRoot = false,
            includeBlocks = false,
            includeInlines = false,
            includeDocument = false,
            includeTarget = true,
            includeText = true,
            range = range,
            direction = direction
        )
    }

    // Original Functions

    fun getNextLeafBlock(path: List<Int>): Node? {
        val iter = blocks(
            targetPath = path,
            direction = Direction.FORWARD,
            includeTarget = false,
            onlyLeaves = true
        ).iterator()
        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    fun getNextLeafBlockByKey(key: String): Node? {
        return getNextLeafBlock(getPathByKey(key))
    }

    fun getPreviousLeafBlock(path: List<Int>): Node? {
        val iter = blocks(
            targetPath = path,
            direction = Direction.BACKWARD,
            includeTarget = false,
            onlyLeaves = true
        ).iterator()
        if (iter.hasNext()) {
            return iter.next()
        }
        return null
    }

    fun getPreviousLeafBlockByKey(key: String): Node? {
        return getPreviousLeafBlock(getPathByKey(key))
    }

    fun mergeNodes(from: List<Int>, to: List<Int> = from.decrementPath()): Node {
        val fromNode: Node = assertNodeByPath(from)
        val toNode: Node = assertNodeByPath(to)

        var newNode: Node? = null
        if (fromNode is TextNode && toNode is TextNode) {
            // if both node is text, merge 'text'
            newNode = toNode.insertText(toNode.text.length, fromNode.text)
        } else if (fromNode is BlockNode && toNode is BlockNode) {
            // if both node is block, merge 'nodes'
            newNode = toNode.updateNodes(toNode.nodes.plus(fromNode.nodes))
        }

        return newNode?.let { removeNode(from).removeNode(to).insertNode(to, it) } ?: this
    }

    /**
     * Merge texts of [from] into [to].
     * Default of [to] is previous node of [from].
     */
    fun mergeTexts(from: List<Int>, to: List<Int> = from.decrementPath()): Node {
        val fromNode: Node = assertNodeByPath(from)
        val toNode: Node = assertNodeByPath(to)

        return if (toNode is TextNode && fromNode is TextNode) {
            toNode.insertText(toNode.text.length, fromNode.text).let { newNode ->
                removeNode(from)
                    .removeNode(to)
                    .insertNode(to, newNode)
            }
        } else this
    }

    /**
     * Merge blocks of [from] into [to]
     */
    fun mergeBlocks(from: List<Int>, to: List<Int>): Node {
        val fromNode: Node = assertNodeByPath(from)
        val toNode: Node = assertNodeByPath(to)

        val newNode: Node =
            toNode.updateNodes(toNode.nodes.orEmpty().plus(fromNode.nodes.orEmpty()))

        return removeNode(from)
            .removeNode(to)
            .insertNode(to, newNode)
    }

    fun equalsIgnoringText(target: Node): Boolean {
        return objectType == target.objectType
                && key == target.key
                && type == target.type
                && data == target.data
                && marks == target.marks
                && nodes.orEmpty().equalsIgnoringText(target.nodes.orEmpty())
    }
}