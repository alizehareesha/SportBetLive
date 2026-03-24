package com.sportbetlive.sgdsn.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sportbetlive.sgdsn.databinding.ActivityAccountRemovalBinding
import com.sportbetlive.sgdsn.util.DataVault

class AccountRemovalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountRemovalBinding
    private val removalPattern = Regex("remove-user-yes")
    private val closeDelayMs = 2000L
    private var removalDetected = false
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { arrayOf(it) }
        } else null
        filePathCallback?.onReceiveValue(resultData)
        filePathCallback = null
    }

    companion object {
        private const val REMOVAL_DESTINATION = "https://avadarasem1.site/remove-user"
        private const val PREFS_NAME = "phone_verification_prefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountRemovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupContentPortal()
        setupCloseButton()
        setupBackNavigation()
        openRemovalPage()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupContentPortal() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.contentPortal, true)
        }

        binding.contentPortal.apply {
            setBackgroundColor(Color.BLACK)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                allowFileAccess = true
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                setSupportZoom(true)
                setSupportMultipleWindows(false)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, link: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, link, favicon)
                    binding.loadingIndicator.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, link: String?) {
                    super.onPageFinished(view, link)
                    binding.loadingIndicator.visibility = View.GONE
                    link?.let { checkForRemovalConfirmation(it) }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.toString()?.let { checkForRemovalConfirmation(it) }
                    return false
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        binding.loadingIndicator.visibility = View.GONE
                    }
                }

                override fun onShowFileChooser(
                    portal: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: FileChooserParams?
                ): Boolean {
                    filePathCallback?.onReceiveValue(null)
                    filePathCallback = callback
                    val intent = params?.createIntent()
                    fileChooserLauncher.launch(intent)
                    return true
                }
            }
        }
    }

    private fun checkForRemovalConfirmation(destination: String) {
        if (!removalDetected && removalPattern.containsMatchIn(destination)) {
            removalDetected = true
            Handler(Looper.getMainLooper()).postDelayed({
                clearAllAppData()
                navigateToPhoneScreen()
            }, closeDelayMs)
        }
    }

    private fun clearAllAppData() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()
        DataVault(this).clearAll()
    }

    private fun navigateToPhoneScreen() {
        val intent = Intent(this, PhoneVerificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCloseButton() {
        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.contentPortal.canGoBack()) {
                    binding.contentPortal.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun openRemovalPage() {
        binding.contentPortal.loadUrl(REMOVAL_DESTINATION)
    }

    override fun onResume() {
        super.onResume()
        binding.contentPortal.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.contentPortal.onPause()
    }

    override fun onDestroy() {
        binding.contentPortal.apply {
            stopLoading()
            clearHistory()
            clearCache(true)
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        super.onDestroy()
    }
}
