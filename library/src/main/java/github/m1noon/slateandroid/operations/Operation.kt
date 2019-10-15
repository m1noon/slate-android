package github.m1noon.slateandroid.operations

import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.NodeProperty
import github.m1noon.slateandroid.models.Selection

abstract class Operation(val updateSelection: Boolean = true, val updateText: Boolean = true) {

    data class AddMark(
        val path: List<Int>,
        val mark: Mark,
        val skipUpdate: Boolean = false
    ) : Operation(updateSelection = false, updateText = !skipUpdate)

    data class InsertNode(
        val path: List<Int>,
        val node: Node
    ) : Operation()

    data class InsertText(
        val path: List<Int>,
        val offset: Int,
        val text: String
    ) : Operation(updateSelection = false, updateText = false)

    data class MergeNode(val from: List<Int>, val to: List<Int>) : Operation()

    data class MoveNode(
        val path: List<Int>,
        val newParentPath: List<Int>,
        val newIndex: Int
    ) : Operation()

    data class RemoveMark(
        val path: List<Int>,
        val mark: Mark,
        val skipUpdate: Boolean = false
    ) : Operation(updateSelection = false, updateText = !skipUpdate)

    data class RemoveNode(val path: List<Int>) : Operation()

    data class RemoveText(
        val path: List<Int>,
        val offset: Int,
        val text: String
    ) : Operation(updateSelection = false, updateText = false)

    data class ReplaceText(
        val path: List<Int>,
        val offset: Int,
        val length: Int,
        val text: String,
        val marks: List<Mark>? = null
    ) : Operation(updateSelection = false, updateText = false)

    data class SetSelection(
        val selection: Selection,
        val skipUpdate: Boolean = false
    ) : Operation(updateSelection = !skipUpdate, updateText = !skipUpdate)

    data class SetNode(
        val path: List<Int>,
        val newProperty: NodeProperty
    ) : Operation()

    data class SplitNode(
        val path: List<Int>,
        val position: Int,
        val property: NodeProperty?,
        val skipUpdate: Boolean = false
    ) : Operation(updateSelection = !skipUpdate, updateText = !skipUpdate)
}
