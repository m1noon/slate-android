package github.m1noon.slateandroid.models

data class InlineNode(
    override val objectType: ObjectType = ObjectType.Inline,
    override val type: Type,
    override val key: String,
    override val nodes: List<Node> = listOf(),
    override val data: Data? = null
) : Node {
    override val text: Nothing? = null
    override val marks: Nothing? = null

    constructor(n: Node) : this(
        type = n.type as Type,
        key = n.key,
        nodes = n.nodes ?: listOf(),
        data = n.data ?: Data.Null
    )

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
     * Type of inline node
     */
    interface Type : Node.Type
}
