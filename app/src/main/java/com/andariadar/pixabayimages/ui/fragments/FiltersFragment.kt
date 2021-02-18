package com.andariadar.pixabayimages.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.andariadar.pixabayimages.R
import com.andariadar.pixabayimages.databinding.FragmentFiltersBinding
import com.andariadar.pixabayimages.ui.PixabayViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class FiltersFragment: Fragment(R.layout.fragment_filters) {
    private val viewModel by hiltNavGraphViewModels<PixabayViewModel>(R.id.navgraph)
    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setFlag(0)

        viewModel.currentColor.observe(viewLifecycleOwner) { allColors ->
            val colors = mutableListOf<CharSequence>()
            colors.addAll(allColors.split(","))

            binding.apply {
                chipGroupColors.children.forEach {
                    val chip = it as Chip
                    colors.let {
                        for(element in colors) {
                            if(chip.text.toString() == element) {
                                chip.isChecked = true
                            }
                        }
                    }
                }
            }
        }

        viewModel.currentOrder.observe(viewLifecycleOwner) { order ->
            binding.apply {
                chipGroupOrder.children.forEach {
                    val chip = it as Chip
                    if(chip.text.toString() == order) {
                        chip.isChecked = true
                    }
                }
            }
        }

        viewModel.currentCategory.observe(viewLifecycleOwner) { category ->
            binding.apply {
                chipGroupCategory.children.forEach {
                    val chip = it as Chip
                    if(chip.text.toString() == category) {
                        chip.isChecked = true
                    }
                }
            }
        }

        binding.apply {
            chipResetColors.setOnClickListener {
                chipGroupColors.clearCheck()
                viewModel.setColor("")
            }

            chipResetCategory.setOnClickListener {
                chipGroupCategory.clearCheck()
                viewModel.setCategory("")
            }

            chipGroupColors.forEach { child ->
                (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                    registerFilterChanged()
                }
            }
        }

        binding.chipGroupCategory.setOnCheckedChangeListener { chipGroup, checkedId ->
            val titleOrNull = chipGroup.findViewById<Chip>(checkedId)?.text.toString()
            viewModel.setCategory(titleOrNull)
        }

        binding.chipGroupOrder.setOnCheckedChangeListener { _, checkedIdOrder ->
            when(checkedIdOrder) {
                R.id.chip_popular -> viewModel.setOrder("popular")
                R.id.chip_latest -> viewModel.setOrder("latest")
            }
        }
    }

    private fun registerFilterChanged() {
        val ids = binding.chipGroupColors.checkedChipIds

        val titles = mutableListOf<CharSequence>()

        ids.forEach { id ->
            titles.add(binding.chipGroupColors.findViewById<Chip>(id).text)
        }

        val text = if (titles.isNotEmpty()) {
            titles.joinToString(",")
        } else {
            ""
        }

        viewModel.setColor(text)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}