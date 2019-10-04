package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Selection

data class ReplaceTextAtRange(
    val range: Selection,
    val length: Int,
    val text: String,
    val marks: List<Mark>? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { c ->
            val start = range.start()
            controller.command(
                ReplaceTextByPath(
                    start.path ?: listOf(),
                    start.offset ?: 0,
                    length,
                    text,
                    marks
                )
            )
        }
    }
}