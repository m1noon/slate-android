package github.m1noon.slateandroid.components

import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.MarkType

/**
 * Returns a marked string with the [text] decorated with [mark].
 */
typealias MarkRenderer = (text: String, mark: Mark) -> String

data class MarkRenderers(val renderers: Map<MarkType, MarkRenderer>) : MarkRenderer {
    override fun invoke(text: String, mark: Mark): String {
        return renderers.get(mark.type)?.invoke(text, mark) ?: text
    }
}

val BoldRenderer: MarkRenderer = {text, _ -> "<b>${text}</b>" }
val StrongRenderer: MarkRenderer = { text, _ -> "<strong>${text}</strong>" }
val ItalicRenderer: MarkRenderer = { text, _ -> "<i>${text}</i>" }

val DefaultMarkRenderer = MarkRenderers(
    mapOf(
        MarkType.BOLD to BoldRenderer,
        MarkType.STRONG to StrongRenderer,
        MarkType.ITALIC to ItalicRenderer
    )
)
