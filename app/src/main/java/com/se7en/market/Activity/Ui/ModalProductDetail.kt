package com.se7en.market.Activity.Ui

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se7en.market.Adapter.ProductSliderAdapter
import com.se7en.market.Utils.Helper
import com.se7en.market.databinding.ModalDetailProductBinding

class ModalProductDetail(private val color: Int) : BottomSheetDialogFragment() {

    private var _binding: ModalDetailProductBinding? = null
    private val binding get() = _binding!!

    private val customShape: Drawable by lazy {
        Helper(requireActivity()).createCustomShape(color, 4f)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupSlider()
        onClickButtons()

    }

    private fun initViews() {

        binding.percentTxt.background = customShape
        binding.addBtn.backgroundTintList = ColorStateList.valueOf(color)
        binding.loadingProgress.setIndicatorColor(color)

    }

    private fun onClickButtons() {

        binding.closeBtn.setOnClickListener { dismiss() }

    }

    private fun setupSlider() {

        val urls = listOf(
            "https://asset.okala.com/unsigned/rs:fill/size:0:0/plain/s3://cdn//product/f16b9a73-9ddd-40bc-ab55-5e71babd95de.jpg",
            "https://asset.okala.com/unsigned/rs:fill/size:0:0/plain/s3://cdn//product/399387.png"
        )
        val dotIndicators = binding.dotsIndicator
        val viewPager = binding.sliderView
        viewPager.adapter = ProductSliderAdapter(urls)
        dotIndicators.visibility = if (urls.size > 1) View.VISIBLE else View.GONE
        dotIndicators.attachTo(viewPager)

    }

}