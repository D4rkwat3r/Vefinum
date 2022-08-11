package com.liquid.vefinum.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.liquid.vefinum.R
import com.liquid.vefinum.adapter.CommunityListAdapter
import com.liquid.vefinum.databinding.FragmentCommunitiesBinding
import com.liquid.vefinum.model.Community
import com.liquid.vefinum.util.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class CommunitiesFragment : Fragment() {

    private lateinit var binding: FragmentCommunitiesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_communities,
            container,
            false
        )
        return setup(binding)
    }

    private fun setup(binding: FragmentCommunitiesBinding): View {
        uiScope.launch { setupRecyclerView(binding) }
        return binding.root
    }

    private suspend fun parseCommunityList(raw: String) = withContext(ioScope.coroutineContext) {
        json.parseToJsonElement(raw)
            .jsonObject["communityList"]
            ?.jsonArray
            ?.filter { it.jsonObject["status"]?.jsonPrimitive?.int == 0 }
            ?.map { it.jsonObject }
            ?.map {
                Community(
                    it["name"]!!.jsonPrimitive.content,
                    it["ndcId"]!!.jsonPrimitive.int,
                    it["icon"]!!.jsonPrimitive.content,
                    it["themePack"]?.jsonObject
                        ?.get("themeColor")
                        ?.jsonPrimitive
                        ?.content
                        ?: "#ffffff",
                    if (it["promotionalMediaList"]?.instanceOf(JsonNull::class) != false) {
                        null
                    } else {
                        it["promotionalMediaList"]!!
                            .jsonArray[0]
                            .jsonArray[1]
                            .jsonPrimitive
                            .content
                    }
                )
            }
    }

    private suspend fun setupRecyclerView(binding: FragmentCommunitiesBinding) {
        val communities = withContext(ioScope.coroutineContext) {
            val response = doGet(
                "g/s/community/joined",
                false,
                mapOf("v" to "1", "start" to "0", "size" to "100")
            )
            parseCommunityList(response.bodyAsText())
        }
        if (communities == null) {
            Toast.makeText(requireContext(), getString(R.string.fetch_communities_error), Toast.LENGTH_SHORT).show()
            return
        }
        val adapter = CommunityListAdapter(communities)
        adapter.onClick = { position ->
            Toast.makeText(requireActivity(), getString(R.string.selected, adapter.communities[position].name), Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.setFragmentResult(
                "selected", bundleOf(
                    "name" to adapter.communities[position].name,
                    "id" to adapter.communities[position].ndcId
                )
            )
        }
        binding.communityList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.communityList.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.communityList.visibility = View.VISIBLE
    }
}