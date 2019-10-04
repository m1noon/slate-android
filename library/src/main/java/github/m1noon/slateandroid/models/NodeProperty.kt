package github.m1noon.slateandroid.models

data class NodeProperty(
    val type: String?,
    val nodes: List<Node>?,
    val data: Map<String, Any>?,
    val text: String?,
    val marks: List<Mark>?
)
