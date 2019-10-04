package github.m1noon.slateandroid.components

import github.m1noon.slateandroid.models.BlockNode

interface BlockComponent : Component {
    fun bindBlockData(data: BlockNode.BlockRenderingData)
}