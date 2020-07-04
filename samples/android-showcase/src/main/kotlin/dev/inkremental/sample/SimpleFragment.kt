package dev.inkremental.sample

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import dev.inkremental.android.Size.*
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.*
import dev.inkremental.dsl.androidx.appcompat.renderable

class SimpleFragment : Fragment() {

    var ticktock = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return renderable {
            linearLayout {
                size(MATCH, MATCH)
                padding(8.dp)
                orientation(LinearLayout.VERTICAL)

                textView {
                    size(MATCH, WRAP)
                    textSize(20f.sp)
                    text("Tick-tock: $ticktock")
                    rippleEffectBorderless(true)
                    clickable(true)
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
            }
        }
    }
}
