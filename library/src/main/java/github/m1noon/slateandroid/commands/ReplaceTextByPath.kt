package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.operations.Operation

data class ReplaceTextByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int,
    val text: String,
    val marks: List<Mark>? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { c ->
            // FIXME crush if target length spans multiple nodes
            c.applyOperation(Operation.ReplaceText(path, offset, length, text, marks))
        }
    }
}
