package github.m1noon.slateandroid.models

sealed class InlineNodeType(label: String) : InlineNode.Type(label) {
    object A : InlineNodeType("a")
}
