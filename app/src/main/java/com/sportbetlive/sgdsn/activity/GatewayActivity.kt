package com.sportbetlive.sgdsn.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.sportbetlive.sgdsn.databinding.ActivityGatewayBinding

class GatewayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGatewayBinding
    private var initialLoadDone = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        val destination = intent.getStringExtra(EXTRA_LINK) ?: ""

        setupPortal()
        binding.contentPortal.loadUrl(destination)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.contentPortal.canGoBack()) {
                    binding.contentPortal.goBack()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupPortal() {
        val portal = binding.contentPortal
        portal.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(portal, true)
        }

        portal.clearCache(true)
        portal.clearHistory()

        portal.webViewClient = object : WebViewClient() {
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

        portal.webChromeClient = WebChromeClient()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    companion object {
        const val EXTRA_LINK = "extra_destination_link"
    }
}
