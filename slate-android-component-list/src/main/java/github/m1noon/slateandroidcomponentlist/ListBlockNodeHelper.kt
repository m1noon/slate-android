package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.TextNode
import java.util.*

fun newOLBlockNode(): BlockNode {
    return BlockNode(
        key = UUID.randomUUID().toString(),
        type = ListBlockNodeType.OL,
        nodes = listOf(
            BlockNode(
                key = UUID.randomUUID().toString(),
                type = ListBlockNodeType.LI,
                nodes = listOf(
                    TextNode(
                        key = UUID.randomUUID().toString()
                    )
                )
            )
        )
    )
}

fun newULBlockNode(): BlockNode {
    return BlockNode(
        key = UUID.randomUUID().toString(),
        type = ListBlockNodeType.UL,
        nodes = listOf(
            BlockNode(
                key = UUID.randomUUID().toString(),
                type = ListBlockNodeType.LI,
                nodes = listOf(
                    TextNode(
                        key = UUID.randomUUID().toString()
                    )
                )
            )
        )
    )
}