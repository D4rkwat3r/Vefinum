package com.liquid.vefinum.model

import com.liquid.vefinum.util.randomDevice
import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val email: String,
    var secret: String,
    val deviceID: String = randomDevice(),
    val clientType: Long = 100,
    val action: String = "normal",
    val timestamp: Long = System.currentTimeMillis(),
    val v: Int = 2
) {
    init {
        secret = "0 $secret"
    }
}
