package github.m1noon.slate_android_component_image

import github.m1noon.slateandroid.models.BlockNode
import java.util.*

fun newImageBlockNode(url: String): BlockNode {
    return BlockNode(
        key = UUID.randomUUID().toString(),
        type = ImageBlockNodeType,
        data = ImageBlockData(url = url)
    )
}
