package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.lift

data class WrapBlockByPath(val path: List<Int>, val block: BlockNode) : FunctionCommand {
    override fun execute(controller: IController) {
        if (path.isEmpty()) {
            return
        }

        val parentPath = path.lift()
        val index = path.last()
        val newPath = path.incrementPath()

        controller.withoutNormalizing { c ->
            c.command(InsertNodeByPath(parentPath, index, block))
            c.command(MoveNodeByPath(newPath, path, 0))
        }
    }
}