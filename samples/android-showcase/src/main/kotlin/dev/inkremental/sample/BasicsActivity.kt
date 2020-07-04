package dev.inkremental.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.android.Size.*
import dev.inkremental.android.renderableContentView
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.*
import dev.inkremental.dsl.androidx.core.CompatTextViewScope
import dev.inkremental.dsl.androidx.core.CompatViewScope

class BasicsActivity : AppCompatActivity() {

    // Our state that we want to render using Inkremental
    var ticktock = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // RenderableView wraps Inkremental and hooks into View lifecycle
        renderableContentView {
            relativeLayout {
                size(MATCH, MATCH)

                linearLayout {
                    size(MATCH, MATCH)
                    padding(8.dp)
                    orientation(LinearLayout.VERTICAL)

                    textView {
                        size(MATCH, WRAP)
                        textSize(20f.sp)
                        text("Tick-tock: $ticktock")
                    }

                    linearLayout {
                        size(MATCH, WRAP)
                        orientation(LinearLayout.HORIZONTAL)

                        button {
                            size(WRAP, WRAP)
                            text("Tick")
                            // onClick conveniently calls Inkremental.render()
                            // after executing listener lambda to render updated state
                            onClick {
                                ticktock++
                            }
                        }

                        button {
                            size(WRAP, 50.sizeDp)
                            margin(8.dp, 0.dp)

                            text("Tock")
                            // You can have more advanced logic here
                            onClick {
                                ticktock = (ticktock - 1).coerceAtLeast(0)
                            }
                        }
                    }

                    // You can use cycles!
                    for (i in 1..ticktock) {
                        textView {
                            size(MATCH, WRAP)
                            padding(4.dp)
                            text("$i")
                            // And conditionals too!
                            if (i % 2 == 1 && i % 5 == 0) {
                                primaryStyle()
                            } else if (i % 2 == 1) { //And do dynamic styling!
                                accentStyle()
                            } else {
                                standardStyle()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun TextViewScope.standardStyle() {
    backgroundColor(Color.TRANSPARENT)
}

fun accentStyle() {
    CompatTextViewScope.textColorCompat(R.color.white)
    CompatViewScope.backgroundColorCompat(R.color.colorAccent)
}

fun primaryStyle() {
    CompatTextViewScope.textColorCompat(R.color.white)
    CompatViewScope.backgroundColorCompat(R.color.colorPrimary)
}
