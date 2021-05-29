package elok.dicoding.made.capstoneproject.ui.components.tv

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import elok.dicoding.made.capstoneproject.MyApplication
import elok.dicoding.made.capstoneproject.R
import elok.dicoding.made.capstoneproject.databinding.FragmentTvBinding
import elok.dicoding.made.capstoneproject.ui.ViewModelFactory
import elok.dicoding.made.core.data.Resource
import elok.dicoding.made.core.domain.model.GenreTv
import elok.dicoding.made.core.domain.model.Tv
import elok.dicoding.made.core.ui.base.BaseFragment
import elok.dicoding.made.core.utils.ext.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

class TvFragment : BaseFragment<FragmentTvBinding>({ FragmentTvBinding.inflate(it) }) {

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: TvViewModel by viewModels { factory }
    private val tvAdapter by lazy { TvAdapter() }

    override fun FragmentTvBinding.onViewCreated(savedInstanceState: Bundle?) {
        binding?.apply {
            rvTv.adapter = tvAdapter
            rvTv.hasFixedSize()
            rvTv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    appbar.isSelected = recyclerView.canScrollVertically(-1)
                }
            })
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    lifecycleScope.launch {
                        viewModel.queryChannel.send(p0.toString())
                    }
                    return true
                }
            })
        }
        tvAdapter.listener = { _, _, item ->
            findNavController().navigate(
                TvFragmentDirections.actionTvFragmentToDetailActivity(tv = item)
            )
        }
        tvAdapter.shareListener = { requireActivity().shareTv(it) }
    }

    override fun observeViewModel() {
        observe(viewModel.genreTvList, ::handleGenreTvList)
        observe(viewModel.tvList, ::handleTvList)
        /*observe(viewModel.search) { searchResult ->
            observe(searchResult?.asLiveData(), ::handleSearch)
        }*/
    }

    private fun handleGenreTvList(genreList: Resource<List<GenreTv>>) {

    }

    private fun handleTvList(tvList: Resource<List<Tv>>) {
        binding?.apply {
            when (tvList) {
                is Resource.Loading -> {
                    errorLayout.gone()
                    loading.root.visible()
                }
                is Resource.Success -> {
                    loading.root.gone()
                    errorLayout.gone()
                    tvAdapter.submitList(tvList.data)
                }
                is Resource.Error -> {
                    loading.root.gone()
                    if (tvList.data.isNullOrEmpty()) {
                        errorLayout.visible()
                        error.message.text =
                            tvList.message ?: getString(R.string.default_error_message)
                    } else {
                        requireContext().showToast(getString(R.string.default_error_message))
                        tvAdapter.submitList(tvList.data)
                    }
                }
            }
        }
    }

    /*private fun handleTvShows(tvShows: Resource<List<MovieTv>>) {
        binding?.apply {
            when (tvShows) {
                is Resource.Loading -> {
                    errorLayout.gone()
                    loading.root.visible()
                }
                is Resource.Success -> {
                    loading.root.gone()
                    errorLayout.gone()
                    tvAdapter.submitList(tvShows.data)
                }
                is Resource.Error -> {
                    loading.root.gone()
                    if (tvShows.data.isNullOrEmpty()) {
                        errorLayout.visible()
                        error.message.text =
                            tvShows.message ?: getString(R.string.default_error_message)
                    } else {
                        requireContext().showToast(getString(R.string.default_error_message))
                        tvAdapter.submitList(tvShows.data)
                    }
                }
            }
        }
    }*/

    /*private fun handleSearch(movies: Resource<List<MovieTv>>) {
        binding?.apply {
            when (movies) {
                is Resource.Loading -> {
                    errorLayout.gone()
                    loading.root.visible()
                }
                is Resource.Success -> {
                    loading.root.gone()
                    errorLayout.gone()
                    tvAdapter.submitList(movies.data)
                }
                is Resource.Error -> {
                    loading.root.gone()
                    if (movies.data.isNullOrEmpty()) {
                        errorLayout.visible()
                        error.message.text =
                            movies.message ?: getString(R.string.default_error_message)
                    } else {
                        requireContext().showToast(getString(R.string.default_error_message))
                        tvAdapter.submitList(movies.data)
                    }
                }
            }
        }
    }*/

    @ExperimentalCoroutinesApi
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onDestroyView() {
        binding?.rvTv?.adapter = null
        super.onDestroyView()
    }
}