package com.se7en.market.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.MapActivity
import com.se7en.market.Activity.SearchActivity
import com.se7en.market.Activity.Ui.AddressListBottomSheet
import com.se7en.market.Adapter.ContainerAdapter
import com.se7en.market.Adapter.SliderAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.SliderModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Response.Home.HomeResponse
import com.se7en.market.ViewModel.HomeViewModel
import com.se7en.market.databinding.FragmentHomeBinding

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var containerAdapter: ContainerAdapter

    private val viewModel by viewModels<HomeViewModel>()

    private var _viewPager: ViewPager2? = null
    private val viewPager get() = _viewPager!!

    private val sliderAdapter = SliderAdapter()
    private var activeAddressTxt: TextView? = null

    private var _context: Context? = null
    private val nonNullContext get() = _context!!

    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (sliderAdapter.itemCount > 0) {
            val nextItem =
                if (viewPager.currentItem == sliderAdapter.itemCount - 1) 0 else viewPager.currentItem + 1
            viewPager.currentItem = nextItem
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        sliderListener()
        dataListener()
        activeAddress()
        buttonsOnCLick()

    }

    private fun dataListener() {
        viewModel.homeStatus.observe(requireActivity()) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    Log.i("TAG", "onViewCreated: loading")
                }

                is NetworkResult.Success -> {
                    setupContainerView(result.response)
                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "onViewCreated: ${result.message}")
                }
            }
        }

        viewModel.getAllData()

    }

    private fun sliderListener() {
        viewModel.sliderStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    initSlider(result.response)
                }

                is NetworkResult.Error -> {

                }
            }
        }
        viewModel.getSliderList()
    }

    private fun initViews() {
        _viewPager = binding.sliderView
        activeAddressTxt = binding.activeAddressTxt
    }

    private fun initSlider(sliderList: List<SliderModel>) {
        val dotsIndicator = binding.dotsIndicator
        sliderAdapter.setItems(sliderList)
        viewPager.adapter = sliderAdapter
        dotsIndicator.attachTo(viewPager)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })
    }

    private fun buttonsOnCLick() {
        activeAddressTxt?.setOnClickListener {
            showAddressListBottomSheet()
        }
        binding.searchViewBtn.setOnClickListener {
            nonNullContext.startActivity(Intent(nonNullContext, SearchActivity::class.java))
        }
    }

    private fun showAddressListBottomSheet(closeable: Boolean = true) {
        AddressListBottomSheet.newInstance(closeable)
            .show(requireActivity().supportFragmentManager, AddressListBottomSheet.TAG)
    }

    private fun activeAddress() {
        viewModel.loadActiveAddress().observe(viewLifecycleOwner) { address ->
            Log.i("TAG", "activeAddress: ${address?.address}")
            if (address != null)
                setActiveAddress(address)
            else {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        MapActivity::class.java
                    )
                )
                requireActivity().finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setActiveAddress(address: AddressModel) {
        val title = if (address.title.isNullOrEmpty()) "آدرس"
        else "${address.title}"
        activeAddressTxt?.text =
            MainActivity().convertToPersianNumbers("$title: \n ${address.address}")
    }

    private fun setupContainerView(response: HomeResponse) {

        val containerView = binding.containerView
        val categoryLayoutManager =
            GridLayoutManager(nonNullContext, 1, GridLayoutManager.HORIZONTAL, false)
        containerAdapter =
            ContainerAdapter(
                getMainList(response.data),
                categoryLayoutManager
            ) { product, position, adapter ->
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.updateItem(position, product)
                }, 3000)
            }
        containerView.adapter = containerAdapter


    }

    private fun getMainList(data: HomeResponse.Data): MutableList<MainListModel> {

        val mainList: MutableList<MainListModel> = mutableListOf()

        val list: MutableList<ListItemModel> = mutableListOf(
            ProductModel(1, "40"),
            ProductModel(2, "50"),
            ProductModel(3, "10"),
            ProductModel(4, "40"),
            ProductModel(5, "20"),
            ProductModel(6, "15"),
            ProductModel(7, "35"),
            ProductModel(8, "20"),
            ProductModel(9, "5"),
            ProductModel(10, "95"),
            ProductModel(11, "70")
        )
        val list2: MutableList<ListItemModel> = mutableListOf(
            ProductModel(21, "40"),
            ProductModel(12, "50"),
            ProductModel(13, "10"),
            ProductModel(14, "40"),
            ProductModel(15, "20"),
            ProductModel(16, "15"),
            ProductModel(71, "35"),
            ProductModel(81, "20"),
            ProductModel(91, "5"),
            ProductModel(110, "95"),
            ProductModel(111, "70")
        )
        val category: MutableList<ListItemModel> = mutableListOf(
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

        when {
            data.shops.isNotEmpty() -> {
                mainList.add(
                    MainListModel(
                        title = "${data.shops.size} فروشگاه اطراف شما ",
                        position = 2,
                        viewType = ViewTypeModel.SHOP_TYPE,
                        items = data.shops as MutableList<ListItemModel>
                    )
                )
            }
        }
        mainList.add(
            MainListModel(
                position = 0,
                viewType = ViewTypeModel.CATEGORY_TYPE,
                items = category
            )
        )

        mainList.add(
            MainListModel(
                position = 1,
                viewType = ViewTypeModel.HEADER_TYPE,
                items = list
            )
        )

        mainList.add(
            MainListModel(
                position = 3,
                viewType = ViewTypeModel.ONE_ROW_TYPE,
                items = list2
            )
        )


        mainList.sortBy { it.position }
        return mainList
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }

}