package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class SetTextByPath(val path: List<Int>, val text: String, val marks: List<Mark>? = null) :
    FunctionCommand {
    override fun execute(controller: IController) {
        val node = controller.getValue().document.assertNodeByPath(path)
        val length = node.text?.length ?: 0
        controller.command(ReplaceTextByPath(path, 0, length, text, marks))
    }
}