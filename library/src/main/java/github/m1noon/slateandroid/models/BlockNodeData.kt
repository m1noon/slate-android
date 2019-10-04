package github.m1noon.slateandroid.models

interface BlockNodeData : Data {
    data class Image(val url: String) : BlockNodeData
}
