package github.m1noon.slateandroid.utils

import github.m1noon.slateandroid.models.Node

fun List<Node>.equalsIgnoringText(target: List<Node>): Boolean {
    if (this.size != target.size) {
        return false
    }
    for (i in this.indices) {
        if (!this[i].equalsIgnoringText(target[i])) {
            return false
        }
    }
    return true
}
