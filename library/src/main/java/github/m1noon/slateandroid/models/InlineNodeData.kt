package github.m1noon.slateandroid.models

interface InlineNodeData : Data {
    data class A(val link: String) : InlineNodeData
}
