package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

data class SplitDescendantByKey(
    val key: String,
    val textPath: List<Int>,
    val textOffset: Int
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.command(
            SplitDescendantByPath(
                controller.getValue().document.getPathByKey(key),
                textPath,
                textOffset
            )
        )
    }
}