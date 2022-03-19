package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    // et = edit text
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharCount = findViewById(R.id.tvCharCount)
        tvCharCount.text = "0/280"

        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener {

            // Grab the content of the etCompose box
            val tweetContent = etCompose.text.toString()

            // Ensure tweet falls within acceptable length params
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT)
                    .show()
                //SnackBar!?
            } else if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long!", Toast.LENGTH_SHORT)
                    .show()
            } else {

                client.publishTweet(tweetContent, object: JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published!")
                        // Send tweet back to TimelineActivity so that refreshing timeline is not necessary
                            // 1. Close activity
                            // 2. Send new tweet to TimelineActivity
                            // 3. Refresh Timeline with new tweet in it (without new API call)
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }



        }

        etCompose.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val tweetLength = etCompose.text.toString().length
                tvCharCount.text = "$tweetLength/280"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(true){
                    // Do nothing
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if(etCompose.text.toString().length > 280) {
                    tvCharCount.setTextColor(Color.parseColor("#FF0000"))
                    Log.i(TAG, "Changing color to red")
                } else {
                    if (tvCharCount.currentTextColor != Color.parseColor("#000000")) {
                        tvCharCount.setTextColor(Color.parseColor("#000000"))
                        Log.i(TAG, "Changing color to black")
                    }
                }
            }
        })
    }

    companion object {
        val TAG = "ComposeActivity"
    }
}