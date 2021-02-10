package com.andariadar.pixabayimages.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.andariadar.pixabayimages.R
import com.andariadar.pixabayimages.adapters.ImagesLoadStateAdapter
import com.andariadar.pixabayimages.adapters.ImagesPagingAdapter
import com.andariadar.pixabayimages.databinding.FragmentImagesBinding
import com.andariadar.pixabayimages.ui.PixabayViewModel
import com.andariadar.pixabayimages.utils.hide
import com.andariadar.pixabayimages.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.net.URLEncoder

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ImagesFragment: Fragment(R.layout.fragment_images) {

    //private val viewModel by viewModels<PixabayViewModel>()
    //private val viewModel by navGraphViewModels<PixabayViewModel>(R.id.navgraph){defaultViewModelProviderFactory}
    private val viewModel by hiltNavGraphViewModels<PixabayViewModel>(R.id.navgraph)
    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private val adapter = ImagesPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                    // Only emit when REFRESH LoadState for RemoteMediator changes.
                    .distinctUntilChangedBy { it.refresh }
                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { binding.recyclerView.scrollToPosition(0) }
        }

        viewModel.currentQuery.observe(viewLifecycleOwner) { queryString ->
            binding.apply {
                chipResetQueries.setOnClickListener {
                    viewModel.setQuery("")
                }

                if(queryString != "") {
                    chipResetQueries.show()
                    searchText.text = getString(R.string.results_for, queryString)
                } else {
                    chipResetQueries.hide()
                    searchText.text = getString(R.string.results_for_all_images)
                }
            }
        }

        binding.retryButton.setOnClickListener {
            adapter.retry()
        }

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = ImagesLoadStateAdapter { adapter.retry() },
                    footer = ImagesLoadStateAdapter { adapter.retry() }
            )

            adapter.addLoadStateListener { loadState ->
                binding.apply {
                    // Only show the list if refresh succeeds.
                    recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                    //searchText.isVisible = loadState.source.refresh is LoadState.NotLoading
                    //chipResetQueries.isVisible = loadState.source.refresh is LoadState.NotLoading
                    // Show loading spinner during initial load or refresh.
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    errorMessage.isVisible = loadState.source.refresh is LoadState.Error
                    retryButton.isVisible = loadState.source.refresh is LoadState.Error
                }
            }
        }

        lifecycleScope.launch {
            viewModel.imagesFlow.collectLatest { images ->
                adapter.submitData(images)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_images, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val encodedQuery = URLEncoder.encode(it, "utf-8")
                    viewModel.setQuery(encodedQuery)
                }

                return true
            }

            override fun onQueryTextChange(p0: String?) = true
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.filters -> {
            val action = ImagesFragmentDirections.actionImagesFragmentToFiltersFragment()
            findNavController().navigate(action)
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}