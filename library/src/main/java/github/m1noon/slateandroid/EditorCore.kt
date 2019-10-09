package github.m1noon.slateandroid

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.children
import github.m1noon.slateandroid.components.*
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.controllers.NewController
import github.m1noon.slateandroid.models.*
import github.m1noon.slateandroid.operations.Operation

open class EditorCore : LinearLayout {
    private val TAG = "EditorCore"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val renderer: Renderer = Renderer(context)
    protected val controller: IController = NewController { c, v, ops ->
        Log.d(TAG, "OnValueChanged: ${ops}")
        logNode(v.document)
        val updateSelection: Boolean = ops.firstOrNull { it.updateSelection } != null

        // render components
        renderValue(c, v, updateSelection)
    }

    fun getValue(): Value {
        return controller.getValue()
    }

    fun applyOperation(operation: Operation) {
        controller.applyOperation(operation)
    }

    fun updateValue(value: Value) {
        controller.setValue(value)
        invalidate(controller.getValue())
    }

    private fun invalidate(value: Value) {
        if (childCount > 0) {
            removeAllViews()
        }
        renderValue(controller, value, true)
    }

    private fun renderValue(c: IController, v: Value, updateSelection: Boolean = false) {
        // render components
        val components = renderer.render(c, v.document)
        components.forEachIndexed { index, component ->
            val view = component.view()

            // add view to layout
            val layoutedIndex = indexOfChild(view)
            if (layoutedIndex == -1) {
                addView(view, index)
            } else if (layoutedIndex != index) {
                detachViewFromParent(layoutedIndex)
                attachViewToParent(view, index, view.layoutParams)
            }

            // update selection
            if (updateSelection) {
                component.syncSelection()
            }
        }

        // remove legacy views
        if (childCount > components.size) {
            removeViews(components.size, childCount - components.size)
        }
    }

    private fun logNode(n: Node, prefix: String = "") {
        Log.d(TAG, "[onValueChanged] ${prefix} ${n.objectType}/${n.type} '${n.text}'")
        for (child in n.nodes.orEmpty()) {
            logNode(child, "${prefix}--")
        }
    }
}