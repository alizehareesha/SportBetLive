package com.sportbetlive.sgdsn.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sportbetlive.sgdsn.databinding.ActivityLoaderBinding

class LoaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
