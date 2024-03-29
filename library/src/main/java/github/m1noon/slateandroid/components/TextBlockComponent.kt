package github.m1noon.slateandroid.components

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import github.m1noon.slateandroid.commands.*
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.databinding.TextBlockBinding
import github.m1noon.slateandroid.models.*
import java.lang.Math.min
import java.util.*

class TextBlockComponent(
    context: Context,
    val controller: IController,
    private var data: BlockNode.BlockRenderingData,
    private var sync: Boolean = false
) : BlockComponent, CustomEditText.Listener, TextWatcher, TextView.OnEditorActionListener {

    private val TAG = "TextBlockComponent"
    private val binding: TextBlockBinding
    private var skippedText: String = ""

    init {
        binding = TextBlockBinding.inflate(LayoutInflater.from(context))
        binding.indicatorText = ""
        binding.editText.addTextChangedListener(this)
        binding.editText.setListener(this)
        binding.editText.setOnEditorActionListener(this)
        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                invalidateSelection()
            }
        }
    }

    override fun view(): View {
        return binding.root
    }

    override fun data(): BlockNode.BlockRenderingData {
        return data
    }

    override fun setSyncState(start: Boolean) {
        sync = start
    }

    override fun bindText(text: String, forceUpdate: Boolean) {
        val htmlText = Html.fromHtml(text)
        val current = binding.editText.text.toString()
        if (forceUpdate || htmlText.toString() != current) {
            val length = htmlText.toString().length
            val start = min(binding.editText.selectionStart, length)
            val end = min(binding.editText.selectionEnd, length)
            // bind text
            binding.editText.setText(htmlText)
            // restore selection
            binding.editText.setSelection(start, end)
            skippedText = ""
        } else {
            skippedText = text
        }
    }

    override fun applyTextAppearance(styleRes: Int) {
        binding.editText.setTextAppearance(binding.root.context, styleRes)
        binding.indicator.setTextAppearance(binding.root.context, styleRes)
    }

    override fun bindBlockData(data: BlockNode.BlockRenderingData) {
        this.data = data
    }

    override fun syncSelection() {
        val selection = controller.getValue().selection
        val anchorKey = selection.anchor.key
        val focusKey = selection.focus.key

        var length = 0
        var anchorPoint: Int? = null
        var focusPoint: Int? = null

        data.nodes.forEach { node: Node ->
            node.texts().forEach { tn ->
                // find point from child nodes
                if (tn.key == anchorKey && anchorPoint == null) {
                    anchorPoint = length + (selection.anchor.offset ?: 0)
                }
                if (tn.key == focusKey && focusPoint == null) {
                    focusPoint = length + (selection.focus.offset ?: 0)
                }

                // update view selection state if all points found
                if (anchorPoint != null && focusPoint != null) {
                    if (binding.editText.text.length < focusPoint!!) {
                        Log.w(TAG, "a selection point cannot be over the text length.")
                        return
                    }

                    binding.editText.requestFocus()
                    binding.editText.setSelection(anchorPoint!!, focusPoint!!)
                    val imm =
                        binding.editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as (InputMethodManager)
                    imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
                    return
                }

                length += tn.text?.length ?: 0
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        Log.d(TAG, "[onSelectionChanged]start='${selStart}', end='${selEnd}'")
        if (!sync) {
            Log.d(TAG, "[onSelectionChanged]skipped")
            return
        }

        var isAnchorSet: Boolean = false
        var isFocusSet: Boolean = false

        // shortcut for obvious case
        if (selStart == 0) {
            headPoint()?.let { controller.command(SetAnchor(it)) }
            isAnchorSet = true
        }
        if (selEnd == 0) {
            headPoint()?.let { controller.command(SetFocus(it)) }
            isFocusSet = true
        }
        if (isAnchorSet && isFocusSet) {
            return
        }

        // loop all text nodes to calc position
        var length = 0
        val value = controller.getValue()
        data.nodes.map { it.key }.let {
            for (key in it) {
                for (tn in value.document.assertNodeByKey(key).texts()) {
                    val textLength = tn.getTextString().length

                    // set anchor (start)
                    if (selStart <= length + textLength && !isAnchorSet) {
                        controller.command(
                            SetAnchor(
                                Point(tn.key, selStart - length, value.document.getPathByKey(key))
                            )
                        )
                        isAnchorSet = true
                    }
                    // set focus (end)
                    if (selEnd <= length + textLength && !isFocusSet) {
                        controller.command(
                            SetFocus(
                                Point(tn.key, selEnd - length, value.document.getPathByKey(key))
                            )
                        )
                        isFocusSet = true
                    }

                    // finish if both points are set
                    if (isAnchorSet && isFocusSet) {
                        return
                    }

                    length += textLength
                }
            }
        }
    }

    override fun onDeleteKeyDown(): Boolean {
        // handle delete operation if selection is head of this block
        if (binding.editText.selectionStart == 0 && binding.editText.selectionEnd == 0) {
            // 1. get older sibling of this leaf block
            // 2. if sibling is not found, find previous leaf block and do process (4)
            // 3. if sibling is inline or text
            //   -> merge this block to parent block TODO is this ok?
            // 4. else if sibling is block
            //   a. if the block is not leaf, get previous leaf block, and do following operation
            //   b. if it is not void, merge this block to it
            //   c. if it is void, remove the void block
            val value = controller.getValue()

            // 1) get previous sibling
            value.document.getPreviousSiblingByKey(data.key)?.let {
                // 3) merge to parent
                if (it.objectType == ObjectType.Inline || it.objectType == ObjectType.Text) {
                    value.document.getParentByKey(data.key)?.let { parent ->
                        controller.command(MergeBlocksByKey(data.key, parent.key))
                        return true
                    }
                }
            }

            // 2), 4.a) get previous leaf block
            value.document.getPreviousLeafBlockByKey(data.key)?.let { prevBlock ->
                if (controller.isVoid(prevBlock.key)) {
                    // 4.c)
                    controller.command(RemoveNodeByKey(prevBlock.key))
                    onSelectionChanged(
                        binding.editText.selectionStart,
                        binding.editText.selectionEnd
                    )
                } else {
                    // 4.b)
                    controller.command(MergeBlocksByKey(data.key, prevBlock.key))
                }
                return true
            }

        }
        return false
    }

    override fun onInputFixed(s: Editable?) {
        // re-render when input fixed by previous skipped-text which is same as fixed text.
        // 'bindText' will dispatch syncing to controller and it will make infinity loop, so stop sync before bind text.
        if (skippedText.isNotEmpty() && sync) {
            setSyncState(false)
            bindText(skippedText, true)
            setSyncState(true)
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        Log.i(TAG, "[onEditorAction]: actionId='${actionId}', event='${event}'")
        if (!sync) {
            return false
        }

        if (event == null) {
            return false
        }

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (isSelectionAtEnd()) {
                        // create new block node to end of parent block.
                        val document = controller.getValue().document
                        document.getParentByKey(data.key)?.let { parent ->
                            // TODO set correct block type
                            val path = document.getPathByKey(parent.key)
                            val blockNodeType = data.type
                            val index = parent.nodes.orEmpty().indexOfFirst { it.key == data.key }
                                .let { if (it == -1) 0 else it }
                            controller.command(
                                InsertNodeByPath(
                                    path,
                                    index + 1,
                                    BlockNode(
                                        key = UUID.randomUUID().toString(),
                                        type = blockNodeType,
                                        nodes = listOf(
                                            TextNode(key = UUID.randomUUID().toString())
                                        ),
                                        data = null
                                    )
                                )
                            )
                        }
                    } else {
                        // split node if current selection is between texts
                        controller.command(SplitBlock())
                    }
                    return true
                }
            }

        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
        /* Do nothing */
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        /*Do nothing*/
    }

    override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d(TAG, "[onTextChange]:${cs}, start:${start}, before:${before}, count:${count}")
        if (!sync) {
            return
        }

        cs?.let { s ->
            if (before == 0) {
                controller.command(InsertText(s.substring(start, start + count)))
            } else {
                if (count > 0) {
                    // replace
                    getTextPoint(start)?.let {
                        controller.command(
                            ReplaceTextByPath(
                                it.path, it.offset ?: 0, before, s.substring(start, start + count)
                            )
                        )
                    }
                } else {
                    // remove
                    getTextPoint(start, true)?.let {
                        val textSize =
                            controller.getValue().document.assertNodeByPath(it.path).text?.length
                                ?: 0

                        if (textSize <= before && data.nodes.size > 1) {
                            // remove node if all text is removed & another node exist
                            controller.command(RemoveNodeByPath(it.path))
                        } else {
                            controller.command(RemoveTextByPath(it.path, it.offset!!, before))
                        }
                    }
                }
            }
        }
    }

    /**
     * set up indicator
     */
    fun bindIndicator(text: String = "") {
        binding.indicatorText = text
    }

    // Set up selection in model by current selection state of view
    // you should make sure that edit text has focus before call this method
    private fun invalidateSelection() {
        onSelectionChanged(binding.editText.selectionStart, binding.editText.selectionEnd)
    }

    private fun headPoint(): Point? {
        return data.nodes.firstOrNull()?.getFirstText()
            ?.let { Point(it.key, 0, controller.getValue().document.getPathByKey(it.key)) }
    }

    private fun getTextPoint(index: Int, lastIncludePrev: Boolean = false): Point? {
        var length = 0
        val value = controller.getValue()
        data.nodes.map { it.key }.let {
            for (key in it) {
                value.document.getNodeByKey(key)?.let { n ->
                    for (tn in n.texts()) {
                        val textLength = tn.getTextString().length
                        if (index < length + textLength) {
                            return Point(
                                tn.key,
                                index - length,
                                value.document.getPathByKey(tn.key)
                            )
                        }
                        length += textLength
                    }
                }
            }
        }
        Log.w(TAG, "text point not found: {$index, $length}")
        return null
    }

    private fun isSelectionAtEnd(): Boolean {
        return binding.editText.selectionStart == binding.editText.text.length
    }
}