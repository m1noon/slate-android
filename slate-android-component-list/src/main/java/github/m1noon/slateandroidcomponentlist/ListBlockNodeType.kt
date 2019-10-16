package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.models.BlockNode

sealed class ListBlockNodeType(label: String) : BlockNode.Type(label) {
    object OL : ListBlockNodeType("ol")
    object UL : ListBlockNodeType("ul")
    object LI : ListBlockNodeType("li")
}
