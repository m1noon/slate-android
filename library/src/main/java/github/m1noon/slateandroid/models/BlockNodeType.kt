package github.m1noon.slateandroid.models

sealed class BlockNodeType(label: String) : BlockNode.Type(label) {
    object HEADING_1 : BlockNodeType("heading1")
    object HEADING_2 : BlockNodeType("heading2")
    object HEADING_3 : BlockNodeType("heading2")
    object PARAGRAPH : BlockNodeType("paragraph")
}
