package dev.inkremental.sample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import dev.inkremental.android.Size.*
import dev.inkremental.android.renderableContentView
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context: Context = this

        // RenderableView wraps Inkremental and hooks into View lifecycle
        renderableContentView {
            linearLayout {
                size(MATCH, MATCH)
                orientation(LinearLayout.VERTICAL)

                imageView {
                    layoutGravity(Gravity.CENTER_VERTICAL)
                    imageResource(R.drawable.ic_logo)
                }

                textView {
                    text("Basics")
                    onClick {
                        startActivity(Intent(context, BasicsActivity::class.java))
                    }

                    size(WRAP, WRAP)
                    padding(16.dp)

                    paintFlags(Paint.UNDERLINE_TEXT_FLAG)
                    textColor(Color.BLUE)
                }

                textView {
                    text("Fragment Sample")
                    onClick {
                        startActivity(Intent(context, FragmentActivity::class.java))
                    }

                    size(WRAP, WRAP)
                    padding(16.dp)

                    paintFlags(Paint.UNDERLINE_TEXT_FLAG)
                    textColor(Color.BLUE)
                }

                textView {
                    text("Lists example")
                    onClick {
                        startActivity(Intent(context, ListActivity::class.java))
                    }

                    size(WRAP, WRAP)
                    padding(16.dp)

                    paintFlags(Paint.UNDERLINE_TEXT_FLAG)
                    textColor(Color.BLUE)
                }

                textView {
                    text("ConstraintsLayout example")
                    onClick {
                        startActivity(Intent(context, ConstraintActivity::class.java))
                    }

                    size(WRAP, WRAP)
                    padding(16.dp)

                    paintFlags(Paint.UNDERLINE_TEXT_FLAG)
                    textColor(Color.BLUE)
                }

                textView {
                    text("YogaLayout example")
                    onClick {
                        startActivity(Intent(context, YogaActivity::class.java))
                    }

                    size(WRAP, WRAP)
                    padding(16.dp)

                    paintFlags(Paint.UNDERLINE_TEXT_FLAG)
                    textColor(Color.BLUE)
                }
            }

        }
    }
}
