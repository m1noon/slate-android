package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType


data class DefaultNodesValidator(
    val min: Int = 0,
    val match: List<SchemaNodeValidator>? = null
) : SchemaNodesValidator {

    constructor(objectType: ObjectType) : this(match = listOf(DefaultNodeValidator(objectType)))
    constructor(objectType: ObjectType, type: Node.Type) : this(
        match = listOf(
            DefaultNodeValidator(
                objectType = objectType,
                type = type
            )
        )
    )

    constructor(validator: SchemaNodeValidator) : this(match = listOf(validator))

    override fun invoke(nodes: List<Node>?, matchNode: Node): ValidationError? {
        // check 'min'
        if (nodes.orEmpty().size < min) {
            return NodesValidationError(kind = SchemaError.Kind.ChildMin)
        }

        // check 'match'
        if (match != null) {
            nodes?.forEachIndexed { index, n ->
                match.forEach { m ->
                    val nodeError = m.invoke(n, matchNode)
                    if (nodeError != null) {
                        return NodesValidationError(
                            kind = nodeError.kind,
                            child = n,
                            index = index
                        )
                    }
                }
            }
        }

        return null
    }
}

private data class NodesValidationError(
    override val kind: SchemaError.Kind,
    val index: Int? = null,
    val child: Node? = null
) : ValidationError {
    override fun toSchemaError(node: Node, rule: SchemaRuleItem): SchemaError {
        if (index != null && child != null) {
            return SchemaError.ChildError(node, rule, kind, index, child)
        }
        return SchemaError.NodeError(node, rule, kind)
    }
}
