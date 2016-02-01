package org.superstring.tangled.pirage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
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
                textView("Howdy?") {
                    onLongClick { toast("test caption"); true }
                }

                button("Start Service"){
                    onClick { owner.startServer() }
                }.lparams(width = matchParent)

                button("Stop Service"){
                    onClick { owner.stopServer() }
                }.lparams(width = matchParent)

                button("Toggle Door"){
                    onClick{ PirageApi.sendClick() }
                }.lparams(width = matchParent)
            }
        }
    }

    fun startServer() = startService<PirageMonitorService>()

    fun stopServer() = stopService(intentFor<PirageMonitorService>())
}
