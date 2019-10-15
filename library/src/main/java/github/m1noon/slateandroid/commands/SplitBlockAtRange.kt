package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.ObjectType
import github.m1noon.slateandroid.models.Selection

data class SplitBlockAtRange(val range: Selection, val height: Int = 1) : FunctionCommand {

    override fun execute(controller: IController) {
        val value = controller.getValue()
        val start = range.start()
        var node = value.document.assertNodeByPath(start.path)
        var parent = value.document.getClosestBlockByKey(node.key)
        var h = 0

        while (parent != null && parent.objectType == ObjectType.Block && h < height) {
            node = parent
            parent = value.document.getClosestBlockByKey(node.key)
            h++
        }

        controller.withoutNormalizing { c ->
            c.command(SplitDescendantByKey(node.key, start.key, start.offset ?: 0))

            // TODO resolve range
        }
    }
}