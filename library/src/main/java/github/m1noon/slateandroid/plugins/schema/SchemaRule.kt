package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.commands.RemoveNodeByKey
import github.m1noon.slateandroid.commands.RemoveNodeByPath
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Data
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType
import github.m1noon.slateandroid.operations.Operation

data class SchemaRule(
    val rules: List<SchemaRuleItem>
) {

    fun isAtomic(node: Node): Boolean {
        return false
    }

    fun isVoid(node: Node): Boolean {
        return findRules(node).firstOrNull { it.isVoid } != null
    }

    fun validateNode(node: Node, controller: IController): SchemaError? {
        val rules = findRules(node)
        rules.forEach { rule ->
            val e = rule.validate(node, controller)
            if (e != null) {
                return e
            }
        }
        return null
    }

    /**
     * Normalize a [node] with [controller].
     * Return normalizer function if some error occurs, else null.
     */
    fun normalizeNode(node: Node, controller: IController): (() -> Unit)? {
        val err = validateNode(node, controller) ?: return null

        return {
            val normalized = err.rule.normalizer?.invoke(controller, err) ?: false
            if (!normalized) {
                defaultNormalize(controller, err)
            }
        }
    }

    private fun findRules(node: Node): List<SchemaRuleItem> {
        return rules.filter { it.match(node) }
    }

    private fun findRule(node: Node): SchemaRuleItem? {
        return rules.firstOrNull { it.match(node) }
    }

    private fun defaultNormalize(controller: IController, err: SchemaError) {
        when (err.kind) {
            else -> controller.command(RemoveNodeByKey(err.node.key))
        }
    }
}

data class SchemaRuleItem(
    // matcher
    val match: SchemaNodeMatcher,
    // property
    val isVoid: Boolean = false,
    // validators
    val nodes: SchemaNodesValidator? = null,
    val data: List<SchemaDataValidator>? = null,
    val text: SchemaTextValidator? = null,
    val marks: SchemaMarksValidator? = null,
    val first: SchemaNodeValidator? = null,
    val last: SchemaNodeValidator? = null,
    val previous: SchemaNodeValidator? = null,
    val next: SchemaNodeValidator? = null,
    // normalizer
    val normalizer: SchemaNormalizer? = null
) {
    fun validate(node: Node, controller: IController): SchemaError? {
        // validate nodes
        nodes?.let { validateNodes ->
            val err = validateNodes(node.nodes, node)?.toSchemaError(node, this)
            if (err != null) {
                return err
            }
        }

        // validate data

        // validate text

        // validate marks

        // validate first (skip if 'valiadtor' or 'nodes.first' is null)
        first?.let { validateFirst ->
            node.nodes?.firstOrNull()?.let { firstNode ->
                val err = validateFirst(firstNode, node)?.let {
                    SchemaError.FirstError(
                        node = node,
                        rule = this,
                        kind = it.kind,
                        child = firstNode
                    )
                }
                if (err != null) {
                    return err
                }
            }
        }

        //validate last
        last?.let { validateLast ->
            node.nodes?.lastOrNull()?.let { lastNode ->
                val err = validateLast(lastNode, node)?.let {
                    SchemaError.LastError(
                        node = node,
                        rule = this,
                        kind = it.kind,
                        child = lastNode
                    )
                }
                if (err != null) {
                    return err
                }
            }
        }

        // validate previous
        previous?.let { validatePrevious ->
            controller.getValue().document.getPreviousSiblingByKey(node.key)?.let { previousNode ->
                val err = validatePrevious(previousNode, node)?.let {
                    SchemaError.PreviousError(
                        node = node,
                        rule = this,
                        kind = it.kind,
                        previous = previousNode
                    )
                }
                if (err != null) {
                    return err
                }
            }
        }

        // validate next
        next?.let { validateNext ->
            controller.getValue().document.getNextSiblingByKey(node.key)?.let { nextNode ->
                val err = validateNext(nextNode, node)?.let {
                    SchemaError.NextError(
                        node = node,
                        rule = this,
                        kind = it.kind,
                        next = nextNode
                    )
                }
                if (err != null) {
                    return err
                }
            }
        }

        return null
    }
}

typealias SchemaNodeMatcher = (node: Node) -> Boolean
typealias SchemaNodeValidator = (target: Node, matchNode: Node) -> ValidationError?
typealias SchemaNodesValidator = (nodes: List<Node>?, matchNode: Node) -> ValidationError?
typealias SchemaDataValidator = (data: Data?, matchNode: Node) -> ValidationError?
typealias SchemaTextValidator = (text: String, matchNode: Node) -> ValidationError?
typealias SchemaMarksValidator = (marks: List<Mark>, matchNode: Node) -> ValidationError?
typealias SchemaNormalizer = (c: IController, err: SchemaError) -> Boolean

data class SchemaNodeMatchers(val list: List<SchemaNodeMatcher>) : SchemaNodeMatcher {
    override fun invoke(node: Node): Boolean {
        return list.firstOrNull { it(node) } != null
    }
}

data class SchemaNodesValidators(val list: List<SchemaNodesValidator>) : SchemaNodesValidator {
    override fun invoke(nodes: List<Node>?, matchNode: Node): ValidationError? {
        list.forEach { validator ->
            val err = validator(nodes, matchNode)
            if (err != null) {
                return err
            }
        }
        return null
    }
}


