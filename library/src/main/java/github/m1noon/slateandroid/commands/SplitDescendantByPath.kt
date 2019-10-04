package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

data class SplitDescendantByPath(
    val path: List<Int>,
    val textPath: List<Int>,
    val textOffset: Int
) : FunctionCommand {
    override fun execute(controller: IController) {
        if (path == textPath) {
            controller.command(SplitNodeByPath(path, textOffset))
            return
        }

        val value = controller.getValue()
        var index = textOffset
        var lastPath = textPath

        controller.withoutNormalizing { c ->
            c.command(SplitNodeByPath(textPath, textOffset))

            val iter = value.document.ancestors(textPath)
            for (ancestorNode in iter) {
                // FIXME include path of node to iterator result for performance
                val ancestorPath = value.document.getPathByKey(ancestorNode.key)
                val target = index

                index = lastPath.last() + 1
                lastPath = ancestorPath

                c.command(SplitNodeByPath(ancestorPath, index))

                if (ancestorPath.equals(path)) {
                    break
                }
            }
        }
    }
}