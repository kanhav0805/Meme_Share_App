package com.example.memeplease

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    var currenturl:String? = null //url to share the image
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //calling loadmeme function
        loadMeme()
    }
    //function to load the meme in the app
    private fun loadMeme()
    {
        //adding loader visible when we call this function
        val bar = findViewById<ProgressBar>(R.id.progressBar)
        bar.visibility = View.VISIBLE
       // Instantiate the RequestQueue.
        //we will use singleton for this
        //val queue = Volley.newRequestQueue(this)//context mein koans activity volley bna rhi hai
        val url = "https://meme-api.herokuapp.com/gimme"
        //meme walla url

        // Request a json response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                //we will enter in this block if we get a response
                currenturl = response.getString("url")//to get the url of image from the api
                //now add url to image view using glide library
                 val memes = findViewById<ImageView>(R.id.memeimg)
                //glide is the culprit which takes more time so we need to remove bar whn it is done
                //here request listener is an interface
                //we need to override its function
                 Glide.with(this).load(currenturl).listener(object:RequestListener<Drawable>{
                     override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>?,
                         isFirstResource: Boolean
                     ): Boolean {
                         //make progressbar gone when no image found
                         bar.visibility = View.GONE
                         //Toast.makeText(this,"Image not loaded",Toast.LENGTH_LONG).show()
                         return false
                     }

                     override fun onResourceReady(
                         resource: Drawable?,
                         model: Any?,
                         target: Target<Drawable>?,
                         dataSource: DataSource?,
                         isFirstResource: Boolean
                     ): Boolean {
                         //when image loaded then also we need to remove the progress bar
                         bar.visibility = View.GONE
                         return false
                     }
                 }).into(memes)
            },
            Response.ErrorListener { error ->
               Toast.makeText(this,"smothing wrong happened",Toast.LENGTH_LONG).show()
            }
        )
// Add the request to the RequestQueue.
        //queue.add(jsonObjectRequest)
        //use mysingleton in this
        Mysingleton.MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
    //function to go on the next meme
    fun Nextmeme(view: View)
    {
        loadMeme()
    }
    //function to share the meme using sharing apps
    fun Sharememe(view: View)
    {
       //now while sharing meme we need the url of the photo
      //which we can get from the loadMeme() function
      //so make the variable global
        //to share the url we use intent
        val intent = Intent(Intent.ACTION_SEND)
        //define type what type of data you want to share
        intent.type = "text/plain"
        //now what message we want to show when it is shared
        intent.putExtra(Intent.EXTRA_TEXT,"HI CHECK THIS MEME FROM REDDIT $currenturl")
        //now we need to create a chooser to choose from which app we want to share
        val chooser = Intent.createChooser(intent,"Send This Meme Using......")
        startActivity(chooser)
    }

}