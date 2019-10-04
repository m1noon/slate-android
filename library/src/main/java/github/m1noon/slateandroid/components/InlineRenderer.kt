package github.m1noon.slateandroid.components

import github.m1noon.slateandroid.models.InlineNode

typealias InlineRenderer = (text: String, node: InlineNode) -> String

data class InlineRenderers(val renderers: Map<InlineNode.Type, InlineRenderer>) : InlineRenderer {
    override fun invoke(text: String, node: InlineNode): String {
        return renderers.get(node.type)?.invoke(text, node) ?: ""
    }
}

val DefaultInlineRenderer = InlineRenderers(mapOf())
