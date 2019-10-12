package github.m1noon.slateandroid.models

data class NodeProperty(
    val type: Node.Type? = null,
    val nodes: List<Node>? = null,
    val data: Data? = null,
    val text: String? = null,
    val marks: List<Mark>? = null
)
