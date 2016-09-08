package org.superstring.tangled.pirage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivityUI().setContentView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    class MainActivityUI : AnkoComponent<MainActivity> {
        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            verticalLayout {
                backgroundResource = R.color.background_material_dark

                button("Toggle Door") {
                    onClick { PirageApi.sendClick() }
                    backgroundResource = R.drawable.btn_green_matte
                }.lparams(height = wrapContent, width = matchParent) { margin = 25 }

                val img = imageView {
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
                        img.imageResource = R.drawable.loading
                        owner.refreshImage(img)
                    }
                }.lparams(height = wrapContent, width = matchParent) { margin = 25 }
            }
        }
    }

    fun refreshImage(view: ImageView) {
        async() {
            info("starting get")
            val image = PirageApi.getImage()
            info("get image got $image")

            runOnUiThread {
                if( image != null )
                    view.imageBitmap = image
                else
                    view.imageResource = R.drawable.fail
            }

        }
    }
}
