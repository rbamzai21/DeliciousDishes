package com.project.deliciousdishes.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.project.deliciousdishes.R
import com.project.deliciousdishes.adapter.RecipeAdapter
import com.project.deliciousdishes.model.RecipeModel
import com.project.deliciousdishes.model.CategoryModel
import kotlinx.android.synthetic.main.recipe_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class SelectCategoryActivity : AppCompatActivity(), RecipeAdapter.onSelectData {

    var recipeAdapter: RecipeAdapter? = null
    var progressDialog: ProgressDialog? = null
    var recipeModel: MutableList<RecipeModel> = ArrayList()
    var categoryModel: CategoryModel? = null
    var strCategory: String? = null
    var strCategoryDescription: String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_list)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        toolbar_filter.setTitle(null)
        setSupportActionBar(toolbar_filter)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Getting Recipe Details")
        progressDialog!!.setMessage("Displaying ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        rvFilter.setLayoutManager(StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL))
        rvFilter.setHasFixedSize(true)

        categoryModel = intent.getSerializableExtra("showFilter") as CategoryModel
        if (categoryModel != null) {
            strCategory = categoryModel!!.strCategory
            strCategoryDescription = categoryModel!!.strCategoryDescription

            tvTitle.setText("$strCategory Recipes")

            filtercategory
        }
    }

    private val filtercategory: Unit
        private get() {
            progressDialog!!.show()

            AndroidNetworking.get("https://prodbyrishab.com/foodapp/{strCategory}/recipe_list.php")
                    .addPathParameter("strCategory", strCategory)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog!!.dismiss()
                                val playerArray = response.getJSONArray("meals")
                                for (i in 0 until playerArray.length()) {

                                    val temp = playerArray.getJSONObject(i)
                                    val dataApi = RecipeModel()
                                    dataApi.idMeal = temp.getString("idMeal")
                                    dataApi.strMeal = temp.getString("strMeal")
                                    dataApi.strMealThumb = temp.getString("strMealThumb")
                                    dataApi.strCategory = temp.getString("strCategory")
                                    recipeModel.add(dataApi)
                                    showFilter()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@SelectCategoryActivity,
                                        "Unable to find resource file recipe list.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@SelectCategoryActivity,"Unable to find recipe. Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    private fun showFilter() {
        recipeAdapter = RecipeAdapter(
            this@SelectCategoryActivity,
            recipeModel,
            this
        )
        rvFilter!!.adapter = recipeAdapter
    }

    override fun onSelected(recipeModelMain: RecipeModel) {
        val intent = Intent(this@SelectCategoryActivity, RecipeDetailsActivity::class.java)
        intent.putExtra("detailRecipe", recipeModelMain)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1_settings -> {
                startActivity(Intent( this, SettingsActivity::class.java))
            }
            // callback for back button
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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