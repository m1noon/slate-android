package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Selection


data class AddMarkAtRange(val range: Selection, val mark: Mark) : FunctionCommand {
    override fun execute(controller: IController) {
        if (range.isCollapsed()) return

        val value = controller.getValue()
        val texts = value.document.getTextsAtRange(range)
        val start = range.start()
        val end = range.end()

        controller.withoutNormalizing { c ->
            texts.forEach { node ->
                var index = 0
                var length = node.text.orEmpty().length

                if (node.key == start.key) index = start.offset ?: 0
                if (node.key == end.key) length = end.offset ?: 0
                if (node.key == start.key && node.key == end.key)
                    length = (end.offset ?: 0) - (start.offset ?: 0)

                c.command(
                    AddMarkByKey(
                        key = node.key, offset = index, length = length, mark = mark
                    )
                )
            }
        }
    }
}