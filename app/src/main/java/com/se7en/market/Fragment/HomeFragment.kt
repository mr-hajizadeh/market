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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.se7en.market.Activity.MainActivity
import com.se7en.market.Activity.MapActivity
import com.se7en.market.Activity.SearchActivity
import com.se7en.market.Activity.Ui.AddressListBottomSheet
import com.se7en.market.Adapter.ContainerAdapter
import com.se7en.market.Adapter.SliderAdapter
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.ListItemModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.SliderModel
import com.se7en.market.Model.ViewTypeModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Response.Home.HomeResponse
import com.se7en.market.ViewModel.HomeViewModel
import com.se7en.market.databinding.FragmentHomeBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var containerAdapter: ContainerAdapter
    private lateinit var containerView: RecyclerView

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
//        containerView.loadSkeleton()
        viewModel.homeStatus.observe(requireActivity()) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    Log.i("TAG", "onViewCreated: loading")
                }

                is NetworkResult.Success -> {
                    setupContainerView(result.response)
//                    Handler(Looper.getMainLooper()).postDelayed(
//                        { containerView.hideSkeleton() },
//                        1000
//                    )
                }

                is NetworkResult.Error -> {
                    Log.i("TAG", "onViewCreated: ${result.message}")
                }
            }
        }
    }

    private fun sliderListener() {
        viewModel.sliderStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.homeContainer.loadSkeleton()
                    binding.appBarLayout.loadSkeleton()
                }

                is NetworkResult.Success -> {
                    initSlider(result.response)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.homeContainer.hideSkeleton()
                        binding.appBarLayout.hideSkeleton()
                    }, 1000)
                }

                is NetworkResult.Error -> {

                }
            }
        }
    }

    private fun initViews() {
        _viewPager = binding.sliderView
        activeAddressTxt = binding.activeAddressTxt
        containerView = binding.containerView
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
            if (address != null) {
                setActiveAddress(address)
                viewModel.getAllData(address.latitude, address.longitude)
                viewModel.getSliderList()
            } else {
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
        val mainList = listOfNotNull(
            data.shops.takeIf { it.isNotEmpty() }?.let {
                MainListModel(
                    title = "${data.shops.size} فروشگاه اطراف شما ",
                    position = 2,
                    viewType = ViewTypeModel.SHOP_TYPE,
                    items = it as MutableList<ListItemModel>
                )
            },
            data.categories.takeIf { it.isNotEmpty() }?.let {
                MainListModel(
                    position = 0,
                    viewType = ViewTypeModel.CATEGORY_TYPE,
                    items = it as MutableList<ListItemModel>
                )
            }
        ).sortedBy { it.position }.toMutableList()


        val list: MutableList<ListItemModel> = mutableListOf(
            ProductModel(1, off_percent = 40),
            ProductModel(2, off_percent = 50),
            ProductModel(3, off_percent = 10),
            ProductModel(4, off_percent = 40),
            ProductModel(5, off_percent = 20),
            ProductModel(6, off_percent = 15),
            ProductModel(7, off_percent = 35),
            ProductModel(8, off_percent = 20),
            ProductModel(9, off_percent = 5),
            ProductModel(10, off_percent = 95),
            ProductModel(11, off_percent = 70)
        )
        val list2: MutableList<ListItemModel> = mutableListOf(
            ProductModel(21, off_percent = 40),
            ProductModel(12, off_percent = 50),
            ProductModel(13, off_percent = 10),
            ProductModel(14, off_percent = 40),
            ProductModel(15, off_percent = 20),
            ProductModel(16, off_percent = 15),
            ProductModel(71, off_percent = 35),
            ProductModel(81, off_percent = 20),
            ProductModel(91, off_percent = 5),
            ProductModel(110, off_percent = 95),
            ProductModel(111, off_percent = 70)
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