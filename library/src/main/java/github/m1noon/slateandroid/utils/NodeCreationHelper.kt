package github.m1noon.slateandroid.utils

import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.BlockNodeType
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.TextNode
import java.util.*

fun newTextBlockNode(): Node {
    return BlockNode(
        key = UUID.randomUUID().toString(),
        type = BlockNodeType.PARAGRAPH,
        nodes = listOf(
            TextNode(
                key = UUID.randomUUID().toString()
            )
        )
    )
}