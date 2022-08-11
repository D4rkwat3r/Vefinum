package com.liquid.vefinum.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.liquid.vefinum.R
import com.liquid.vefinum.databinding.FragmentRunBinding
import com.liquid.vefinum.model.BaseResponse
import com.liquid.vefinum.model.CreateItem
import com.liquid.vefinum.model.UploadMediaResponse
import com.liquid.vefinum.util.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import kotlinx.serialization.decodeFromString

class RunFragment : Fragment() {

    private val premium = false
    private val text = "\n\n[BC]Пост создан при помощи приложения Vefinum, скачивай на этом канале: https://t.me/DWReaction\n[BC]Автор: https://t.me/D4rkwat3r"
    private lateinit var binding: FragmentRunBinding
    private var selectedId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_run,
            container,
            false
        )
        return setup(binding)
    }

    private suspend fun copyAndGet(link: String): String? {
        val bytes = try {
            client.request {
                method = HttpMethod.Get
                url(link)
            }.readBytes()
        } catch (e: Exception) {
            return null
        }
        val response = doPost("g/s/media/upload", false, ContentType.Application.OctetStream.toString(), bytes)
        val data = json.decodeFromString<UploadMediaResponse>(response.bodyAsText())
        return data.mediaValue
    }

    private fun setup(binding: FragmentRunBinding): View {
        requireActivity().supportFragmentManager.setFragmentResultListener("selected", this) { _, data ->
            selectedId = data.getInt("id")
            binding.selectedCommunity.text = getString(R.string.selected_community, data.getString("name"))
        }
        binding.launchButton.setOnClickListener {
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Вы не выбрали сообщество", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uiScope.launch { startSpam() }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private suspend fun startSpam() {
        val count = binding.countField.text.toString().toIntOrNull() ?: 0
        if (count > 500 || count <= 0) {
            Toast.makeText(requireContext(), getString(R.string.incorrect_count), Toast.LENGTH_SHORT).show()
            return
        }
        val image = withContext(ioScope.coroutineContext) {
            copyAndGet(binding.imageField.text.toString())
        }
        if (image == null) {
            Toast.makeText(requireContext(), getString(R.string.incorrect_image), Toast.LENGTH_SHORT).show()
            return
        }
        binding.counter.text = "0/$count"
        repeat(count) {
            ioScope.launch {
                val response = doPostJson(
                    "x$selectedId/s/item", false, CreateItem(
                        binding.titleField.text.toString(),
                        if (premium) binding.contentField.text.toString() else binding.contentField.text.toString() + text,
                        image,
                        buildJsonArray { addJsonArray { add(100); add(image); add(JsonNull) } }
                    )
                )
                val data = try {
                    json.decodeFromString<BaseResponse>(response.bodyAsText())
                } catch (e: Exception) {
                    null
                }
                if (data != null && data.apiStatusCode == 0) {
                    uiScope.launch { incrementCounter() }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun incrementCounter() {
        val parts = binding.counter.text.toString().split("/")
        val oldCreated = parts[0].toInt()
        val oldMax = parts[1].toInt()
        binding.counter.text = "${oldCreated + 1}/$oldMax"
    }
}