package org.superstring.tangled.pirage

import android.widget.ImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * The main UI in Anko
 */
class MainActivityUI : AnkoComponent<MainActivity> {
    lateinit var image: ImageView

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        verticalLayout {
            backgroundResource = R.color.background_material_dark

            button("Toggle Door") {
                onClick {
                    isEnabled = false
                    tryClick(ctx)
                    isEnabled = true
                }
                backgroundResource = R.drawable.btn_green_matte
            }.lparams(height = wrapContent, width = matchParent) { margin = 25 }

            image = imageView {
                imageResource = R.drawable.loading
                scaleType = ImageView.ScaleType.FIT_XY
                adjustViewBounds = true
            }.lparams(height = wrapContent, width = matchParent) {
                leftMargin = 25
                rightMargin = 25
            }

            button("Get Image") {
                backgroundResource = R.drawable.btn_green_matte

                onClick {
                    isEnabled = false
                    owner.refreshImage(image)
                    isEnabled = true
                }
            }.lparams(height = wrapContent, width = matchParent) { margin = 25 }
        }
    }
}
