package org.superstring.tangled.pirage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.setContentView
import org.superstring.tangled.pirage.api.getImage
import timber.log.pirage.info

class MainActivity : AppCompatActivity() {
    lateinit var view: MainActivityUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = MainActivityUI()
        view.setContentView(this)
    }

    override fun onStart() {
        super.onStart()
        launch(UI) { refreshImage(view.image) }
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

    suspend fun refreshImage(view: ImageView) {
        view.imageResource = R.drawable.loading
        info { "starting get" }
        val image = getImage()
        info { "get image got $image" }

        if (image != null)
            view.imageBitmap = image
        else
            view.imageResource = R.drawable.fail
    }
}
