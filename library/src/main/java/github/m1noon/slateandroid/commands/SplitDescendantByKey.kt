package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

data class SplitDescendantByKey(
    val key: String,
    val textKey: String,
    val textOffset: Int
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.command(
            SplitDescendantByPath(
                controller.getValue().document.getPathByKey(key),
                controller.getValue().document.getPathByKey(textKey),
                textOffset
            )
        )
    }
}