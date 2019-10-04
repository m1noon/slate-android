package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Selection

data class InsertTextAtRange(
    val range: Selection, // TODO Range?
    val text: String,
    val marks: List<Mark>? = null
) :
    FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { controller ->
            val value = controller.getValue()
            val start = range.start()

            // TODO return if parent is void

            controller.command(
                InsertTextByPath(
                    start.path,
                    start.offset ?: 0,
                    text,
                    marks
                )
            )
        }
    }
}