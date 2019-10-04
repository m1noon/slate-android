package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class ReplaceText(
    val offset: Int,
    val length: Int,
    val text: String,
    val marks: List<Mark>? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val marks = this.marks ?: value.selection.marks
        controller.withoutNormalizing { c ->
            c.command(ReplaceTextAtRange(value.selection, length, text, marks))
        }
    }
}