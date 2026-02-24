package com.sportbetlive.sgdsn.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sportbetlive.sgdsn.databinding.ActivityLoaderBinding
import com.sportbetlive.sgdsn.util.DataVault
import com.sportbetlive.sgdsn.util.DeviceInfoCollector
import com.sportbetlive.sgdsn.util.ServerBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoaderBinding
    private lateinit var vault: DataVault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vault = DataVault(this)

        val existingKey = vault.getAccessKey()
        if (!existingKey.isNullOrEmpty()) {
            val link = vault.getContentLink()
            if (!link.isNullOrEmpty()) {
                openGateway(link)
                return
            }
        }

        fetchFromServer()
    }

    private fun fetchFromServer() {
        val collector = DeviceInfoCollector(this)
        val bridge = ServerBridge(collector)

        CoroutineScope(Dispatchers.IO).launch {
            val response = bridge.executeRequest()
            withContext(Dispatchers.Main) {
                handleResponse(response)
            }
        }
    }

    private fun handleResponse(response: String?) {
        if (response.isNullOrEmpty()) {
            openMain()
            return
        }

        if (response.contains("#")) {
            val parts = response.split("#", limit = 2)
            val key = parts[0]
            val link = parts[1]
            vault.saveAccessKey(key)
            vault.saveContentLink(link)
            openGateway(link)
        } else {
            vault.savePolicyLink(response)
            openMain()
        }
    }

    private fun openGateway(link: String) {
        val intent = Intent(this, GatewayActivity::class.java)
        intent.putExtra(GatewayActivity.EXTRA_LINK, link)
        startActivity(intent)
        finish()
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
