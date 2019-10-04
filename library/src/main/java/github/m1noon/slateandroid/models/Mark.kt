package github.m1noon.slateandroid.models

data class Mark(
    val objectType: ObjectType = ObjectType.Mark,
    val type: Type,
    val data: Data? = null
) {
    interface Type
}
