package com.sportbetlive.sgdsn.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ServerBridge(private val collector: DeviceInfoCollector) {

    fun buildRequestAddress(): String {
        val base = "https://gtappinfo.site/a-sportbetlive/server.php"
        val params = StringBuilder()
        params.append("?p=Jh675eYuunk85")
        params.append("&os=${encode(collector.getOsVersion())}")
        params.append("&lng=${encode(collector.getLanguage())}")
        params.append("&loc=${encode(collector.getRegion())}")
        params.append("&devicemodel=${encode(collector.getDeviceModel())}")
        params.append("&bs=${encode(collector.getBatteryStatus())}")
        params.append("&bl=${encode(collector.getBatteryLevel())}")
        params.append("&nc=${encode(collector.getNetworkCountry())}")
        params.append("&sm=${encode(collector.getSimState())}")
        return base + params.toString()
    }

    fun executeRequest(): String? {
        var connection: HttpURLConnection? = null
        return try {
            val address = buildRequestAddress()
            connection = (URL(address).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 15000
                useCaches = false
                setRequestProperty("Cache-Control", "no-cache")
                setRequestProperty("Pragma", "no-cache")
            }
            val code = connection.responseCode
            if (code == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                response.trim()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun encode(value: String): String =
        java.net.URLEncoder.encode(value, "UTF-8")
}
