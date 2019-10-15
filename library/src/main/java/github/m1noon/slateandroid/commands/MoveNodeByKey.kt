package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

data class MoveNodeByKey(val key: String, val newParentKey: String, val newIndex: Int) :
    FunctionCommand {
    override fun execute(controller: IController) {
        val document = controller.getValue().document
        controller.command(
            MoveNodeByPath(
                document.getPathByKey(key),
                document.getPathByKey(newParentKey),
                newIndex
            )
        )
    }
}