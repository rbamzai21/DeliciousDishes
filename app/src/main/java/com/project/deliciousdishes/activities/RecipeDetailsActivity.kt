package com.project.deliciousdishes.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.project.deliciousdishes.R
import com.project.deliciousdishes.model.RecipeModel
import kotlinx.android.synthetic.main.detailed_recipe.*
import org.json.JSONException
import org.json.JSONObject


@Suppress("DEPRECATION")
class RecipeDetailsActivity : AppCompatActivity() {

    var idMeal: String? = null
    var strCategory: String? = null
    var strMeal: String? = null
    var recipeModel: RecipeModel? = null
    var progressDialog: ProgressDialog? = null
    lateinit var toolbar: Toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onRestart() {
        super.onRestart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val switch = prefs.getBoolean("musicsetting", false)

        if (switch)
        {
            try {
                AudioPlay.playAudio()

            } catch (ex: Exception) { }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_recipe)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        toolbar_detail.title = null
        setSupportActionBar(toolbar_detail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Getting Recipe Details")
        progressDialog!!.setMessage("Displaying ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)

        recipeModel = intent.getSerializableExtra("detailRecipe") as RecipeModel
        if (recipeModel != null) {
            idMeal = recipeModel!!.idMeal
            strMeal = recipeModel!!.strMeal
            strCategory = recipeModel!!.strCategory

            tvTitle.text = strMeal
            tvTitle.isSelected = true

            Glide.with(this)
                    .load(recipeModel!!.strMealThumb)
                    .placeholder(R.drawable.ic_foodimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgThumb)

            recipeDetails
        }
    }

    private val recipeDetails: Unit

        private get() {

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val switch = prefs.getBoolean("musicsetting", false)

            if (switch)
            {
                try {
                    AudioPlay.playAudio()

                } catch (ex: Exception) { }
            }

            AndroidNetworking.get("https://prodbyrishab.com/foodapp/{strCategory}/{idMeal}.php")
                    .addPathParameter("strCategory", strCategory)
                    .addPathParameter("idMeal", idMeal)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog!!.dismiss()
                                val playerArray = response.getJSONArray("meals")
                                for (i in 0 until playerArray.length()) {

                                    val temp = playerArray.getJSONObject(i)
                                    val Instructions = temp.getString("strInstructions")
                                    tvInstructions!!.text = Instructions

                                    val Category = temp.getString("strCategory")
                                    val Area = temp.getString("strArea")
                                    tvSubTitle!!.text = "Category - $Category"

                                    for (n in 1 .. 20){
                                        val ingredient = temp.getString("strIngredient$n")
                                        val measure = temp.getString("strMeasure$n")
                                        if (ingredient.trim() != "" && ingredient.trim() != "null") tvIngredients.append("\n $n. \t $ingredient")
                                        if (measure.trim() != "" && measure.trim() != "null") tvMeasure.append("\n $measure")
                                    }

                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@RecipeDetailsActivity,
                                        "Unable to find resource file meal details.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@RecipeDetailsActivity,"Unable to find recipe. Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1_settings -> {
//                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            // callback for back button
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        AudioPlay.stopAudio()
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