package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class AddMarkByKey(
    val key: String,
    val offset: Int,
    val length: Int,
    val mark: Mark
) :
    FunctionCommand {
    override fun execute(controller: IController) {
        val path = controller.getValue().document.getPathByKey(key)
        controller.command(AddMarksByPath(path, offset, length, listOf(mark)))
    }
}