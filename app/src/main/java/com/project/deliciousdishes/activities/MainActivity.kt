package com.project.deliciousdishes.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.project.deliciousdishes.R
import com.project.deliciousdishes.adapter.CategorySelectAdapter
import com.project.deliciousdishes.model.CategoryModel
import kotlinx.android.synthetic.main.category_list.*
import kotlinx.android.synthetic.main.start_screen.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), CategorySelectAdapter.onSelectData {

    var categorySelectAdapter: CategorySelectAdapter? = null
    var progressDialog: ProgressDialog? = null
    var categoryModel: MutableList<CategoryModel> = ArrayList()
    lateinit var searchView: SearchView
    lateinit var toolbar: Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1_settings -> {
                startActivity(Intent( this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            AudioPlay.stopAudio()
//            Toast.makeText(this, "Home button pressed", Toast.LENGTH_SHORT).show()
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Toast.makeText(this, "Back button pressed", Toast.LENGTH_SHORT).show()
            AudioPlay.stopAudio()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_list)

        // Adds toolbar title and setting menu
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        AudioPlay.initAudio()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Getting Recipe Details")
        progressDialog!!.setMessage("Displaying ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        val mLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        rvMainMenu.setLayoutManager(mLayoutManager)
        rvMainMenu.setHasFixedSize(true)

        categorylist
    }

    private val categorylist: Unit
        private get() {
            progressDialog!!.show()
            AndroidNetworking.get("https://prodbyrishab.com/foodapp/categories.php")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog!!.dismiss()
                                val playerArray = response.getJSONArray("categories")
                                for (i in 0 until playerArray.length()) {

                                    val temp = playerArray.getJSONObject(i)
                                    val dataApi = CategoryModel()
                                    dataApi.strCategory = temp.getString("strCategory")
                                    dataApi.strCategoryThumb = temp.getString("strCategoryThumb")
                                    dataApi.strCategoryDescription = temp.getString("strCategoryDescription")
                                    categoryModel.add(dataApi)
                                    showCategories()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@MainActivity,
                                        "Unable to find resource file categories.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@MainActivity, "Unable to find recipe. Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    private fun showCategories() {
        categorySelectAdapter =
            CategorySelectAdapter(
                this@MainActivity,
                categoryModel,
                this
            )
        rvMainMenu!!.adapter = categorySelectAdapter
    }

    override fun onSelected(categoryModel: CategoryModel) {
        val intent = Intent(this@MainActivity, SelectCategoryActivity::class.java)
        intent.putExtra("showFilter", categoryModel)
        startActivity(intent)
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }

}