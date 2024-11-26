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
import com.se7en.market.Model.ShopModel
import com.se7en.market.Model.SliderModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.HomeViewModel
import com.se7en.market.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var containerAdapter: ContainerAdapter

    private val viewModel by viewModels<HomeViewModel>()

    private var _viewPager: ViewPager2? = null
    private val viewPager get() = _viewPager!!

    private var _sliderAdapter: SliderAdapter? = null
    private val sliderAdapter get() = _sliderAdapter!!
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

    override fun onStart() {
        super.onStart()
//        discountTimer.start()
    }

    override fun onStop() {
        super.onStop()
//        discountTimer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupContainerView()
        viewModel.getSliderList()
        sliderListener()
//        setupRecommendedView()
//        setupDiscountView()
//        setupShopList()
        activeAddress()
//        binding.collapsingToolbar.loadSkeleton()
//        binding.homeContainer.loadSkeleton()
//        binding.categoryView.loadSkeleton(R.layout.category_view_holder) {
//            itemCount(3)
//        }
//
//        Handler(Looper.getMainLooper()).postDelayed({
//        }, 1000)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            binding.collapsingToolbar.hideSkeleton()
//            binding.homeContainer.hideSkeleton()
//            binding.categoryView.hideSkeleton()
        buttonsOnCLick()
//        }, 3000)
    }

    private fun sliderListener() {
        viewModel.sliderStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    Log.i("TAG", "sliderListener: ${result.response.size}")
                    initSlider(result.response)
                }

                is NetworkResult.Error -> {

                }
            }

        }
    }

    private fun initViews() {
        _viewPager = binding.sliderView
        activeAddressTxt = binding.activeAddressTxt
    }

    private fun initSlider(sliderList: List<SliderModel>) {
        val dotsIndicator = binding.dotsIndicator
        _sliderAdapter = SliderAdapter(sliderList)
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

    private fun setupContainerView() {

        val containerView = binding.containerView
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
        val shopList: MutableList<ListItemModel> = mutableListOf(
            ShopModel(0, "سوپر مارکت 1"),
            ShopModel(0, "سوپر مارکت 2"),
            ShopModel(0, "سوپر مارکت 3"),
            ShopModel(0, "سوپر مارکت 4"),
            ShopModel(0, "سوپر مارکت 5"),
            ShopModel(0, "سوپر مارکت 6"),
            ShopModel(0, "سوپر مارکت 7"),
            ShopModel(0, "سوپر مارکت 8")
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

        val lists = mutableListOf(
            MainListModel(
                "pos 0",
                0,
                ViewTypeModel.CATEGORY_TYPE,
                category
            ),
            MainListModel(
                "pos 2",
                2,
                ViewTypeModel.HEADER_TYPE,
                list
            ),
            MainListModel(
                "pos 4",
                4,
                ViewTypeModel.HEADER_TYPE,
                list2
            ),
            MainListModel(
                "pos 0",
                1,
                ViewTypeModel.SHOP_TYPE,
                shopList
            ),
            MainListModel(
                "pos 1",
                3,
                ViewTypeModel.ONE_ROW_TYPE,
                list
            ),
            MainListModel(
                "pos 5",
                5,
                ViewTypeModel.ONE_ROW_TYPE,
                list2
            ),
            MainListModel(getString(R.string.title_recommended), 5, ViewTypeModel.BRAND_TYPE, list)
        ).apply { sortBy { it.position } }
        val categoryLayoutManager =
            GridLayoutManager(nonNullContext, 1, GridLayoutManager.HORIZONTAL, false)
        containerAdapter =
            ContainerAdapter(lists, categoryLayoutManager) { product, position, adapter ->
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.updateItem(position, product)
                }, 3000)
            }
        containerView.adapter = containerAdapter


    }

    /*@SuppressLint("UseCompatLoadingForDrawables")
    private fun setupRecommendedView() {

        val recommendedView = binding.recommendedView
        val grid = GridLayoutManager(nonNullContext, 2, GridLayoutManager.HORIZONTAL, false)
        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2
                else 1
            }
        }
        recommendedView.layoutManager = grid

        val list = mutableListOf(
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
        recommendedAdapter = ProductAdapter(
            list,
            nonNullContext.getString(R.string.title_recommended),
            nonNullContext.getDrawable(R.drawable.ic_favorite)!!,
            nonNullContext.getColor(R.color.green)
        ) { position ->
            Handler(Looper.getMainLooper()).postDelayed({
                recommendedAdapter.updateItem(position)
            }, 3000)
        }
        recommendedView.adapter = recommendedAdapter
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupDiscountView() {

        setupDiscountTimer()
        val discountView = binding.discountView
        val girdLayout = GridLayoutManager(nonNullContext, 2, GridLayoutManager.HORIZONTAL, false)
        girdLayout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1
            }
        }
        discountView.layoutManager = girdLayout

        val list = mutableListOf(
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
        discountAdapter = ProductAdapter(
            list,
            getString(R.string.title_many_discount),
            nonNullContext.getDrawable(R.drawable.ic_percent)!!,
            nonNullContext.getColor(R.color.orange)
        ) { position ->
            Handler(Looper.getMainLooper()).postDelayed({
                discountAdapter.updateItem(position)
            }, 3000)
        }

        discountView.adapter = discountAdapter

    }

    private fun setupDiscountTimer() {

        val timerView = binding.timerViewTxt
        timerView.text = MainActivity().convertToPersianNumbers(formatTimer(0))
        discountTimer = object : CountDownTimer(10000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerView.text =
                    MainActivity().convertToPersianNumbers(formatTimer(millisUntilFinished))
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }

    }

    private fun formatTimer(timeMillis: Long): String {
        val days = timeMillis / (24 * 60 * 60 * 1000)
        val hours = (timeMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (timeMillis % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (timeMillis % (60 * 1000)) / 1000

        return if (days > 0)
            String.format(Locale.getDefault(), "%02d : %02d : %02d", minutes, hours, days)
        else
            String.format(Locale.getDefault(), "%02d : %02d : %02d", seconds, minutes, hours)

    }

    private fun setupShopList() {
        val shopView = binding.shopView
        val shopBtn = binding.shopBtn

        val list = mutableListOf(
            "سوپر مارکت 1",
            "سوپر مارکت 2",
            "سوپر مارکت 3",
            "سوپر مارکت 4",
            "سوپر مارکت 5",
            "سوپر مارکت 6",
            "سوپر مارکت 7",
            "سوپر مارکت 8"
        )
        val shopAdapter = ShopAdapter(list)
        shopView.adapter = shopAdapter
        shopBtn.text = "${list.size} فروشگاه "
    }*/

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }

}