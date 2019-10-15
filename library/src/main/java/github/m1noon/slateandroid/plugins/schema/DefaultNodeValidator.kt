package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType

data class DefaultNodeValidator(
    val objectTypes: Set<ObjectType>,
    val types: Set<Node.Type>? = null,
    val first: List<SchemaNodeValidator>? = null
) : SchemaNodeValidator {

    constructor(objectType: ObjectType) : this(objectTypes = setOf(objectType))
    constructor(objectType: ObjectType, type: Node.Type) : this(
        objectTypes = setOf(objectType),
        types = setOf(type)
    )

    constructor(
        objectType: ObjectType,
        types: Set<Node.Type>
    ) : this(objectTypes = setOf(objectType), types = types)

    override fun invoke(targetNode: Node, matchNode: Node): ValidationError? {
        // check objectType
        if (!objectTypes.contains(targetNode.objectType)) {
            return NodeValidationError(SchemaError.Kind.ObjectType)
        }

        // check type
        if (types?.contains(targetNode.type) == false) {
            return NodeValidationError(SchemaError.Kind.Type)
        }

        // check first
        first?.forEach { firstValidator ->
            val err = targetNode.nodes?.firstOrNull()?.let { firstValidator(it, targetNode) }
            // FIXME child error?
            if (err != null) {
                return err
            }
        }

        return null
    }
}
