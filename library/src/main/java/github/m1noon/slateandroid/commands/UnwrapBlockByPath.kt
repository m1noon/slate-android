package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.Data
import github.m1noon.slateandroid.models.Point
import github.m1noon.slateandroid.models.Selection

data class UnwrapBlockByPath(
    val path: List<Int>,
    val type: BlockNode.Type? = null,
    val data: Data? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val node = value.document.assertNodeByPath(path)
        // FIXME error will occurs if node has no text
        val first = node.getFirstText()!!
        val last = node.getLastText()!!
        // FIXME smart selection creation
        val range = Selection(
            anchor = Point(
                key = first.key,
                path = value.document.getPathByKey(first.key),
                offset = 0
            ),
            focus = Point(
                key = last.key,
                path = value.document.getPathByKey(last.key),
                offset = 0
            )
        )
        controller.command(UnwrapBlockAtRange(range, type, data))
    }
}