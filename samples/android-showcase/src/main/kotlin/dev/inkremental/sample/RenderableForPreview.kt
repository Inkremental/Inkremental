package dev.inkremental.sample

import android.content.Context
import dev.inkremental.android.CENTER
import dev.inkremental.android.RenderableView
import dev.inkremental.android.Size.MATCH
import dev.inkremental.android.Size.WRAP
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.frameLayout
import dev.inkremental.dsl.android.widget.textView

class RenderableForPreview(context: Context) : RenderableView(context) {

    var state = ""

    init {
        if (isInEditMode) {
            state = "preview"
        } else {
            state = "live"
        }
    }

    override val renderable = {
        frameLayout {
            size(MATCH, MATCH)
            textView {
                size(WRAP, WRAP)
                layoutGravity(CENTER)
                text("Hello Inkremental from $state")
            }
        }
    }
}
