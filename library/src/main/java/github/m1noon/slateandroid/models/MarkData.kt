package github.m1noon.slateandroid.models

interface MarkData : Data {
    data class Color(val rgb: String) : MarkData
}
