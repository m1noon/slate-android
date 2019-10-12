package github.m1noon.slateandroid.models

data class Document(
    override val objectType: ObjectType = ObjectType.Document,
    override val key: String = "",
    override val nodes: List<Node> = listOf()
) : Node {
    override val type: Nothing? = null
    override val text: Nothing? = null
    override val marks: Nothing? = null
    override val data: Nothing? = null

    override fun updateKey(key: String): Node {
        return copy(key = key)
    }

    override fun updateNodes(nodes: List<Node>): Node {
        return copy(nodes = nodes)
    }

    override fun mergeProperties(property: NodeProperty): Node {
        return copy(
            nodes = property.nodes ?: this.nodes
        )
    }
}
