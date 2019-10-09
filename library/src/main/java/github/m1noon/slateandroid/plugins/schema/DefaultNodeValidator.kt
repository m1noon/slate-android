package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType

data class DefaultNodeValidator(
    val objectTypes: Set<ObjectType>,
    val first: List<SchemaNodeValidator>? = null
) : SchemaNodeValidator {

    constructor(objectType: ObjectType) : this(objectTypes = setOf(objectType))

    override fun invoke(targetNode: Node, matchNode: Node): ValidationError? {
        // check objectType
        if (!objectTypes.contains(targetNode.objectType)) {
            return NodeValidationError(SchemaError.Kind.ObjectType)
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
