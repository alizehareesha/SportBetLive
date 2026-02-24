package com.sportbetlive.sgdsn.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sportbetlive.sgdsn.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPolicyBinding
    private var initialLoadDone = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val destination = intent.getStringExtra(EXTRA_POLICY_LINK) ?: ""

        binding.policyPortal.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        binding.policyPortal.clearCache(true)

        binding.policyPortal.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, pageAddress: String?, favicon: Bitmap?) {
                super.onPageStarted(view, pageAddress, favicon)
                if (!initialLoadDone) {
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, pageAddress: String?) {
                super.onPageFinished(view, pageAddress)
                if (!initialLoadDone) {
                    initialLoadDone = true
                    binding.loadingOverlay.visibility = View.GONE
                }
            }
        }

        if (destination.isNotEmpty()) {
            binding.policyPortal.loadUrl(destination)
        }
    }

    companion object {
        const val EXTRA_POLICY_LINK = "extra_policy_destination"
    }
}
