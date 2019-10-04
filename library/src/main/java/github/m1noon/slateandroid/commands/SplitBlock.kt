package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

data class SplitBlock(val height: Int = 1) : FunctionCommand {
    override fun execute(controller: IController) {
        // TODO delete expanded
        val value = controller.getValue()

        controller.command(SplitBlockAtRange(value.selection, height))
        // TODO selection moveToEnd

        // TODO select marks
    }
}