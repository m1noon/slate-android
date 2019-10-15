package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.commands.FunctionCommand
import github.m1noon.slateandroid.commands.SplitNodeByPath
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.lift

data class UnwrapListItemByPath(val path: List<Int>) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val parentPath = path.lift()
        val parentBlock = value.document.assertNodeByPath(parentPath)
        val index = path.last()
        var newPath = path

        controller.withoutNormalizing { c ->
            if (index != parentBlock.nodes.orEmpty().size) {
                c.command(SplitNodeByPath(parentPath, index + 1))
            }
            if (index != 0) {
                c.command(SplitNodeByPath(parentPath, index))
                newPath = path.incrementPath()
            }
            // TODO unwrap

            // TODO set li to paragraph
        }
    }
}