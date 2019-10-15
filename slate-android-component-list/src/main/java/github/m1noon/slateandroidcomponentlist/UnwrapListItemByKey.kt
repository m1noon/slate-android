package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.commands.FunctionCommand
import github.m1noon.slateandroid.commands.SetNodeByKey
import github.m1noon.slateandroid.commands.UnwrapBlockByKey
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNodeType
import github.m1noon.slateandroid.models.NodeProperty

data class UnwrapListItemByKey(val key: String) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { c ->
            c.command(UnwrapBlockByKey(key))
            c.command(SetNodeByKey(key, NodeProperty(type = BlockNodeType.PARAGRAPH)))
        }
    }
}
