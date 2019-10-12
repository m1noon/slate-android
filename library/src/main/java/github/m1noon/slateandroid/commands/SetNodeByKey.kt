package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty

data class SetNodeByKey(val key: String, val newProperties: NodeProperty) : FunctionCommand {
    override fun execute(controller: IController) {
        val path = controller.getValue().document.getPathByKey(key)
        controller.command(SetNodeByPath(path, newProperties))
    }
}
