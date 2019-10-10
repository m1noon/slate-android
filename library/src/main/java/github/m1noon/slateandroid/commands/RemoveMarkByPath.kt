package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class RemoveMarkByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int,
    val mark: Mark
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.command(RemoveMarksByPath(path, offset, length, listOf(mark)))
    }
}