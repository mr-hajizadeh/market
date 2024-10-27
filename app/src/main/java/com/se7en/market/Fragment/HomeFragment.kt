package com.se7en.market.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.se7en.market.Activity.Ui.AddressListBottomSheet
import com.se7en.market.Adapter.CategoryAdapter
import com.se7en.market.Adapter.SliderAdapter
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.SliderModel
import com.se7en.market.R
import com.se7en.market.databinding.FragmentHomeBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.collapsingToolbar.loadSkeleton()
        binding.homeContainer.loadSkeleton()
        binding.categoryView.loadSkeleton(R.layout.category_view_holder) {
            itemCount(3)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.collapsingToolbar.hideSkeleton()
            binding.homeContainer.hideSkeleton()
            binding.categoryView.hideSkeleton()
            initCategory()
            initSlider()
            buttonsOnCLick()
        }, 3000)
    }

    private fun initCategory() {
        binding.categoryView.adapter = CategoryAdapter(
            listOf(
                CategoryModel(R.drawable.t1, "میوه و سبزیجات تازه"),
                CategoryModel(R.drawable.t2, "لبنیات و تخم مرغ"),
                CategoryModel(R.drawable.t3, "نوشیدنی‌ها"),
                CategoryModel(R.drawable.t1, "گوشت و مرغ"),
                CategoryModel(R.drawable.t2, "مواد غذایی کنسروی"),
                CategoryModel(R.drawable.t3, "نان و شیرینی"),
                CategoryModel(R.drawable.t1, "آجیل و خشکبار"),
                CategoryModel(R.drawable.t2, "غذاهای یخ‌زده"),
                CategoryModel(R.drawable.t3, "مواد غذایی کودک"),
                CategoryModel(R.drawable.t1, "مواد شوینده و بهداشتی"),
                CategoryModel(R.drawable.t2, "محصولات پروتئینی"),
                CategoryModel(R.drawable.t3, "آرایشی و بهداشتی"),
                CategoryModel(R.drawable.t1, "لوازم خانه و آشپزخانه"),
                CategoryModel(R.drawable.t2, "حبوبات و غلات"),
                CategoryModel(R.drawable.t3, "سس‌ها و چاشنی‌ها"),
                CategoryModel(R.drawable.t1, "تنقلات و میان‌وعده‌ها"),
                CategoryModel(R.drawable.t2, "ادویه‌جات و افزودنی‌ها"),
                CategoryModel(R.drawable.t3, "غذاهای آماده و نیمه‌آماده"),
                CategoryModel(R.drawable.t1, "محصولات کم‌کالری و رژیمی"),
                CategoryModel(R.drawable.t2, "لوازم التحریر و اداری")
            )
        )
    }

    private fun initSlider() {
        val viewPager = binding.sliderView
        val sliderAdapter = SliderAdapter(
            listOf(
                SliderModel(R.drawable.t1),
                SliderModel(R.drawable.t2),
                SliderModel(R.drawable.t3)
            )
        )
        viewPager.adapter = sliderAdapter
        binding.dotsIndicator.attachTo(viewPager)
    }

    private fun buttonsOnCLick() {
        binding.addressBtn.setOnClickListener {
            showAddressListBottomSheet()
        }
    }

    private fun showAddressListBottomSheet(closeable: Boolean = true) {
        AddressListBottomSheet.newInstance(closeable)
            .show(requireActivity().supportFragmentManager, AddressListBottomSheet.TAG)
    }

}