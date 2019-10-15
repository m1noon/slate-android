package github.m1noon.slateandroid.controllers

import android.util.Log
import github.m1noon.slateandroid.commands.Command
import github.m1noon.slateandroid.commands.FunctionCommand
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType
import github.m1noon.slateandroid.models.Value
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.operations.Operator
import github.m1noon.slateandroid.plugins.schema.SchemaRule
import github.m1noon.slateandroid.utils.getAncestorPaths
import github.m1noon.slateandroid.utils.getAncestorPathsWithMe
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.transform
import java.lang.RuntimeException


interface IController {
    fun setup(config: Config)
    fun command(c: Command): IController
    fun applyOperation(operation: Operation): IController
    fun flush(): IController
    fun normalize(): IController
    fun getValue(): Value
    fun setValue(value: Value, forceNormalize: Boolean? = null): IController
    fun updateValue(fn: (Value) -> Value): IController
    fun withoutNormalizing(fn: (IController) -> Unit): IController
}

fun newController(
    onChange: (
        IController, Value, List<Operation>
    ) -> Unit
): IController {
    return Controller(onChange)
}

private class Controller(
    val onChange: (IController, Value, List<Operation>) -> Unit,
    var _value: Value = Value(),
    val operator: Operator = Operator()
) : IController {

    private data class Tmp(
        val dirty: List<List<Int>> = listOf(),
        val flushing: Boolean = false,
        val merge: Any? = null,
        val normalize: Boolean = true,
        val save: Boolean = true
    )

    lateinit var schemaRule: SchemaRule
    var readOnly: Boolean = false
    var operations: List<Operation> = listOf()
    var tmp: Tmp = Tmp()

    override fun setup(config: Config) {
        this.schemaRule = config.schemaRule
        this.readOnly = config.readOnly
    }

    override fun applyOperation(operation: Operation): IController {
//        // Save the operation into the history. Since `save` is a command, we need
//        // to do it without normalizing, since it would have side effects.
//        this.withoutNormalizing(() => {
//            controller.save(operation)
//            value = this.value
//        })

        // Apply the operation to the value.
        this._value = operator.applyOperation(_value, operation)
        operations = operations.plus(operation)

        // Get the paths of the affected nodes, and mark them as dirty.
        val newDirtyPaths = getDirtyPaths(operation)
        val dirty: MutableList<List<Int>> = mutableListOf()
        this.tmp.dirty.forEach { path ->
            path.transform(operation).forEach { transformed ->
                dirty.add(transformed)
            }
        }
        // PERF: De-dupe the paths so we don't do extra normalization.
        val pathIndex: MutableMap<String, Boolean> = mutableMapOf()
        val dirtyPaths = newDirtyPaths.plus(dirty).filter { path ->
            val key = path.joinToString(",")
            if (pathIndex[key] == null) {
                pathIndex[key] = true
                true
            } else false
        }
        this.tmp = this.tmp.copy(dirty = dirtyPaths)

        // If we're not already, queue the flushing process on the next tick.
        if (!tmp.flushing) {
            tmp = tmp.copy(flushing = true)
            flush()
        }

        return this
    }

    override fun flush(): IController {
        val v = _value
        val ops = operations
        operations = listOf()
        tmp = tmp.copy(flushing = false)
        onChange(this, v, ops)
        return this
    }

    override fun command(c: Command): IController {
        if (c is FunctionCommand) {
            c.execute(this)
            normalizeDirtyPaths()
            Log.d("Controller", "Controller.command:Finished: {command=${c}}")
            return this
        }
        // TODO handle by middleware
        return this
    }

    /**
     * Normalize all of the nodes in the document from scratch.
     */
    override fun normalize(): IController {
        val value = this._value
        val table = value.document.getKeysToPathTable()
        val paths = table.values
        this.tmp = this.tmp.copy(dirty = this.tmp.dirty.plus(paths))

        normalizeDirtyPaths()

        return this
    }

    override fun getValue(): Value {
        return _value
    }

    override fun setValue(value: Value, forceNormalize: Boolean?): IController {
        val normalize: Boolean = forceNormalize ?: this._value != value
        this._value = value

        // normalize
        if (normalize) {
            this.normalize()
        }

        return this
    }

    override fun updateValue(fn: (Value) -> Value): IController {
        return setValue(fn(getValue()))
    }

    /**
     * Apply a series of changes inside a synchronous `fn`, deferring
     * normalization until after the function has finished executing.
     */
    override fun withoutNormalizing(fn: (IController) -> Unit): IController {
        val normalize = this.tmp.normalize
        this.tmp = this.tmp.copy(normalize = false)
        fn(this)
        this.tmp = this.tmp.copy(normalize = normalize)
        normalizeDirtyPaths()
        return this
    }

    /**
     * Get the "dirty" paths for a given `operation`.
     *
     * @param {Operation} operation
     * @return {Array}
     */
    fun getDirtyPaths(operation: Operation): List<List<Int>> {
        val value = getValue()
        return when (operation) {
            // TODO add other operations
            is Operation.AddMark -> operation.path.getAncestorPathsWithMe()
            is Operation.InsertText -> operation.path.getAncestorPathsWithMe()
            is Operation.RemoveMark -> operation.path.getAncestorPathsWithMe()
            is Operation.RemoveText -> operation.path.getAncestorPathsWithMe()
            is Operation.SetNode -> operation.path.getAncestorPathsWithMe()
            is Operation.InsertNode -> {
                val path = operation.path
                val table = operation.node.getKeysToPathTable()
                val nodePaths = table.values.map { path.plus(it) }
                return path.getAncestorPathsWithMe().plus(nodePaths)
            }
            is Operation.SplitNode -> {
                val nextPath = operation.path.incrementPath()
                return operation.path.getAncestorPathsWithMe().plus(listOf(nextPath))
            }
            is Operation.MergeNode -> {
                val descendantPaths = value.document.descendants(operation.to)
                    .map { value.document.getPathByKey(it.key) }
                return operation.from.getAncestorPaths() + operation.to.getAncestorPathsWithMe() + descendantPaths
            }
            is Operation.MoveNode -> {
                if (operation.path == operation.newParentPath
                    || operation.path == operation.newParentPath.plus(operation.newIndex)
                ) {
                    return listOf()
                }
                val oldAncestors = operation.path.getAncestorPaths()
                val newAncestorsWithMe =
                    operation.newParentPath.plus(operation.newIndex).getAncestorPathsWithMe()
                return oldAncestors + newAncestorsWithMe

            }
            is Operation.RemoveNode -> operation.path.getAncestorPaths()
            else -> listOf()
        }
    }

    /**
     * Normalize any new "dirty" paths that have been added to the change.
     */
    fun normalizeDirtyPaths() {
        if (!tmp.normalize) {
            return
        }
        if (tmp.dirty.isEmpty()) {
            return
        }
        withoutNormalizing {
            while (this.tmp.dirty.isNotEmpty()) {
                val path = this.tmp.dirty.get(0)
                this.tmp = this.tmp.copy(dirty = this.tmp.dirty.drop(1))
                normalizeNodeByPath(path)
            }
        }
    }

    /**
     * Normalize the node at a specific [givenPath].
     */
    fun normalizeNodeByPath(givenPath: List<Int>) {
        var path = givenPath
        val value = getValue()
        var node: Node? = value.document.getNodeByPath(path) ?: return
        var iterations = 0
        val max = 100 + if (node?.objectType == ObjectType.Text) 1 else node?.nodes.orEmpty().size

        while (node != null) {
            // TODO plugin
            val normalizer = schemaRule.normalizeNode(node, this) ?: break

            normalizer()

            val newValue = getValue()
            val key = node.key
            val newNode = newValue.document.getDescendantByPath(path)

            if (newNode != null && newNode.key == key) {
                node = newNode
            } else {
                val newNodeByKey = newValue.document.getNodeByKey(key)
                if (newNodeByKey != null) {
                    node = newNodeByKey
                    path = value.document.getPathByKey(key)
                } else {
                    break
                }
            }

            iterations++
            if (iterations > max) {
                throw RuntimeException("A schema rule could not be normalized after sufficient iterations. This is usually due to a `rule.normalize` or `plugin.normalizeNode` function of a schema being incorrectly written, causing an infinite loop.")
            }
        }
    }
}