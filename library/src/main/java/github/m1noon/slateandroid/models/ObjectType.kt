package github.m1noon.slateandroid.models


enum class ObjectType(val value: String) {
    Value("value"),
    Document("document"),
    Block("block"),
    Inline("inline"),
    Text("text"),
    Mark("mark");

    companion object {
        fun fromString(value: String): ObjectType {
            return values().first { it.value == value }
        }
    }

    override fun toString(): String {
        return value
    }
}
