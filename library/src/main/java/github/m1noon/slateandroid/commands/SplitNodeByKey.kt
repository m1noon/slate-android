package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty

data class SplitNodeByKey(
    val key: String,
    val position: Int,
    val property: NodeProperty? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val path = controller.getValue().document.getPathByKey(key)
        controller.command(SplitNodeByPath(path, position, property))
    }
}