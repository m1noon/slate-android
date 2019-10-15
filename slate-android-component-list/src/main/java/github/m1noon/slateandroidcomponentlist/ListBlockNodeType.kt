package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.models.BlockNode

sealed class ListBlockNodeType : BlockNode.Type {
    object OL : ListBlockNodeType()
    object UL : ListBlockNodeType()
    object LI : ListBlockNodeType()
}
