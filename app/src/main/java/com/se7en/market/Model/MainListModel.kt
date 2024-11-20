package com.se7en.market.Model

data class MainListModel(
    val title: String,
    val position: Int = 0,
    val viewType: ViewTypeModel = ViewTypeModel.HEADER_TYPE,
    val items: MutableList<ListItemModel>,
)