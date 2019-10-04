package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Selection

data class SetTextAtRange(
    val range: Selection,
    val text: String,
    val marks: List<Mark>? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val start = range.start()
        controller.command(
            SetTextByPath(start.path ?: listOf(), text, marks)
        )
    }
}