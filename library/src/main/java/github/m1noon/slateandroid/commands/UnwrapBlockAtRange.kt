package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.*

/**
 * Unwrap all of the block nodes in a [range] from a block with properties such as [type] or [data].
 */
data class UnwrapBlockAtRange(
    val range: Rangeable,
    val type: BlockNode.Type? = null,
    val data: Data? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val blocks = value.document.getLeafBlocksAtRange(range)
        val wrappers = blocks.map {
            value.document.getClosestByKey(it.key) {
                it.objectType == ObjectType.Block && (type == null || type == it.type) && (data == null || data == it.data)
            }
        }.filterNotNull()

        controller.withoutNormalizing { c ->
            wrappers.forEach { block ->
                val first = block.nodes.orEmpty().first()
                val last = block.nodes.orEmpty().last()
                val parent =
                    controller.getValue().document.getParentByKey(block.key) ?: return@forEach
                val index = parent.nodes.orEmpty().indexOfFirst { it.key == block.key }
                    .let { if (it == -1) 0 else it }

                // filtered children which are in range
                val children = block.nodes.orEmpty().filter { child ->
                    blocks.firstOrNull { it.key == child.key || child.hasDescendant(it.key) } != null
                }

                val firstMatch = children.first()
                val lastMatch = children.last()

                if (first == firstMatch && last == lastMatch) {
                    block.nodes?.forEachIndexed { i, child ->
                        c.command(MoveNodeByKey(child.key, parent.key, index + i))
                    }
                    c.command(RemoveNodeByKey(block.key))
                } else if (last == lastMatch) {
                    var match: Boolean = false
                    block.nodes?.filter {
                        // skip nodes before first match
                        match = match || it == firstMatch
                        match
                    }?.forEachIndexed { i, child ->
                        c.command(MoveNodeByKey(child.key, parent.key, index + 1 + i))
                    }
                } else if (first == firstMatch) {
                    var match = false
                    block.nodes?.filter {
                        match = match || it == lastMatch
                        !match
                    }
                        ?.plus(lastMatch)
                        ?.forEachIndexed { i, child ->
                            c.command(MoveNodeByKey(child.key, parent.key, index + i))
                        }
                } else {
                    val firstText = firstMatch.getFirstText()!!
                    c.command(SplitDescendantByKey(block.key, firstText.key, 0))

                    children.forEachIndexed { i, child ->
                        // First child block is split to two nodes, and first is empty, second has original content which is newly created,
                        // So merge second to first.
                        if (i == 0) {
                            c.getValue().document.getNextBlockByKey(child.key)?.let { secondChild ->
                                c.command(MergeBlocksByKey(secondChild.key, child.key))
                            }
                        }
                        c.command(MoveNodeByKey(child.key, parent.key, index + 1 + i))
                    }
                }
            }
        }
    }
}