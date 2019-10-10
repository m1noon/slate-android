package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.operations.Operation

data class InsertTextByPath(
    val path: List<Int>,
    val offset: Int,
    val text: String,
    val marks: List<Mark>? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { controller ->
            // TODO do something for annotation

            controller.applyOperation(Operation.InsertText(path, offset, text))

            marks?.let {
                if (it.isNotEmpty()) {
                    controller.command(ReplaceMarksByPath(path, offset, text.length, it, true))
                }
            }
        }
    }
}