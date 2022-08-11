package com.liquid.vefinum.util

import android.util.Base64
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val webUserAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36 Edg/101.0.1210.53"
private const val mobileUserAgent: String = "Dalvik/2.1.0 (Linux; U; Android 7.1.2; SM-G977N Build/beyond1qlteue-user 7; com.narvii.amino.master/3.4.33597)"
private val ndcPrefix: ByteArray = Base64.decode("Qg==", Base64.NO_WRAP)
private val ndcSigKey: ByteArray = Base64.decode("+OemGsP3JZQeOsfK4taIvpfzC5M=", Base64.NO_WRAP)
private val ndcDeviceKey: ByteArray = Base64.decode("ArJYxjVZ2IBDIcXVBlrzIDWNNm8=", Base64.NO_WRAP)
val client: HttpClient = HttpClient(Android)
val uiScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
val json: Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
}
var sid: String? = null
typealias UrlParams = Map<String, String>

fun randomDevice(): String {
    val token = ByteArray(15)
    Random().nextBytes(token)
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(SecretKeySpec(ndcDeviceKey, "HmacSHA1"))
    mac.update(ndcPrefix)
    mac.update(token)
    return ndcPrefix.toHexString() + token.toHexString() + mac.doFinal().toHexString()
}

fun requestSignature(data: ByteArray): String {
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(SecretKeySpec(ndcSigKey, "HmacSHA1"))
    mac.update(data)
    return Base64.encodeToString(ndcPrefix + mac.doFinal(), Base64.NO_WRAP)
}

suspend fun doRequest(requestMethod: String,
                      web: Boolean,
                      endpoint: String,
                      contentType: String?,
                      body: ByteArray?,
                      params: UrlParams? = null) = client.request {
    method = HttpMethod.parse(requestMethod)
    url {
        protocol = URLProtocol.HTTP
        host = if (web) "aminoapps.com" else "service.narvii.com"
        path(if (web) "api/$endpoint" else "api/v1/$endpoint")
        params?.forEach { parameters.append(it.key, it.value) }
    }
    headers {
        append("Accept-Language", "ru-RU")
        if (web) {
            append("X-Requested-With", "XMLHttpRequest")
        } else {
            append("NDCDEVICEID", randomDevice())
            append("NDCLANG", "ru")
        }
        if (sid != null) {
            append(if (web) "cookie" else "NDCAUTH", "sid=$sid")
        }
        append("User-Agent", if (web) webUserAgent else mobileUserAgent)
        if (body != null) {
            if (!web) append("NDC-MSG-SIG", requestSignature(body))
            setBody(body)
        }
        }
    if (contentType != null) contentType(ContentType.parse(contentType))
}

suspend fun doGet(endpoint: String, web: Boolean, params: UrlParams? = null) = doRequest(
    "GET",
    web,
    endpoint,
    null,
    null,
    params
)

suspend fun doPost(endpoint: String, web: Boolean, contentType: String, body: ByteArray, params: UrlParams? = null) = doRequest(
    "POST",
    web,
    endpoint,
    contentType,
    body,
    params
)

suspend inline fun <reified T> doPostJson(endpoint: String, web: Boolean, body: T, params: UrlParams? = null): HttpResponse {
    val data = json.encodeToString(body)
    return doPost(endpoint, web, ContentType.Application.Json.toString(), data.toByteArray(Charsets.UTF_8), params)
}
