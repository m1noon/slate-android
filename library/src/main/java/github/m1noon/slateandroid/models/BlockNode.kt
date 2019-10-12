package github.m1noon.slateandroid.models

import github.m1noon.slateandroid.utils.equalsIgnoringText

data class BlockNode(
    override val objectType: ObjectType = ObjectType.Block,
    override val type: Type,
    override val key: String = "",
    override val nodes: List<Node> = listOf(),
    override val data: Data? = null
) : Node {
    override val text: Nothing? = null
    override val marks: Nothing? = null

    override fun updateKey(key: String): Node {
        return copy(key = key)
    }

    override fun updateNodes(nodes: List<Node>): Node {
        return copy(nodes = nodes)
    }

    override fun mergeProperties(property: NodeProperty): Node {
        var t = this.type
        property.type?.let {
            if (it is Type) {
                t = it as Type
            }
        }
        return copy(
            type = t,
            nodes = property.nodes ?: this.nodes,
            data = property.data ?: this.data
        )
    }

    /**
     * Type of block node
     */
    interface Type : Node.Type

    /**
     * Data to use when render BlockNode
     */
    data class BlockRenderingData(
        // key of leaf BlockNode
        val key: String,
        // position of this group in leaf block
        val index: Int,
        // type of leaf BlockNode
        val type: Type,
        // children nodes of this block. all items of this list is 'inline' or 'text', not 'block'
        val nodes: List<Node>,
        // data of leaf BlockNode
        val data: Data?,
        // parent data list of this block.
        val parents: List<ParentBlock>
    ) {
        data class ParentBlock(
            val key: String,
            val type: Type,
            val data: Data?
        )

        fun equalsIgnoringText(target: BlockRenderingData): Boolean {
            return key == target.key && index == target.index && type == target.type
                    && this.data == target.data
                    && parents == target.parents
                    && nodes.equalsIgnoringText(target.nodes)
        }
    }

    fun getBlockRenderingData(parents: List<BlockRenderingData.ParentBlock> = listOf()): List<BlockRenderingData> {
        val list: MutableList<BlockRenderingData> = mutableListOf()
        val children: MutableList<Node> = mutableListOf()

        val addChildren: () -> Unit = {
            list.add(
                BlockRenderingData(key, list.size, type, children.toList(), data, parents)
            )
            children.clear()
        }

        for (n in nodes) {
            when (n) {
                is BlockNode -> {
                    if (children.isNotEmpty()) {
                        addChildren()
                    }
                    list.addAll(
                        n.getBlockRenderingData(
                            listOf(BlockRenderingData.ParentBlock(this.key, this.type, this.data))
                        )
                    )
                }
                else -> {
                    children.add(n)
                }
            }
        }
        if (children.isNotEmpty()) {
            addChildren()
        }
        return list
    }
}