package com.taonce.wankotlin.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.taonce.utilmodule.showDebug
import com.taonce.wankotlin.R
import com.taonce.wankotlin.base.BaseBean
import com.taonce.wankotlin.base.BaseMVPActivity
import com.taonce.wankotlin.bean.db.CollectionDB
import com.taonce.wankotlin.contract.ICollectionListView
import com.taonce.wankotlin.presenter.CollectionListPresenter
import com.taonce.wankotlin.ui.adapter.CollectionListAdapter
import kotlinx.android.synthetic.main.activity_collection_list.*
import kotlinx.android.synthetic.main.item_common_head.*


class CollectionListActivity : BaseMVPActivity<ICollectionListView, CollectionListPresenter>(), ICollectionListView {
    private val mPresenter: CollectionListPresenter by lazy { getPresenter() }
    private lateinit var mAdapter: CollectionListAdapter
    private val mCollectionListBean: MutableList<CollectionDB> = mutableListOf()
    private var index: Int = 0
    private var deletePosition: Int = 0

    override fun getLayoutId(): Int = R.layout.activity_collection_list

    override fun initData() {
        mPresenter.getCollectionList(index)
    }

    override fun initView() {
        tv_head_title.text = getString(R.string.collection)
        mAdapter = CollectionListAdapter(
            this@CollectionListActivity,
            R.layout.item_collection_list,
            mutableListOf()
        )
        rv_collection_list.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@CollectionListActivity)
        }
    }

    override fun initEvent() {
        iv_head_back.setOnClickListener { finish() }
        mAdapter.setOnItemClickListener {
            val item: CollectionDB = mCollectionListBean[it]
            toCommonX5Activity(
                this@CollectionListActivity,
                url = item.url,
                isCollected = true,
                articleId = item.articleId,
                collectTime = item.collectTime,
                publishTime = item.publishTime,
                author = item.author,
                title = item.title
            )
        }
        mAdapter.setCancelListener {
            deletePosition = it
            val itemData: CollectionDB = mCollectionListBean[it]
            mPresenter.cancelCollection(itemData.articleId, itemData.originId)
        }
    }

    override fun getPresenter(): CollectionListPresenter = CollectionListPresenter(this)

    override fun showCollectionList(collectionBean: MutableList<CollectionDB>) {
        isShowNull(false)
        index++
        mCollectionListBean.addAll(collectionBean)
        mAdapter.addListData(collectionBean)
    }

    override fun showNull() {
        isShowNull()
    }

    private fun isShowNull(flag: Boolean = true) {
        if (flag) {
            tv_collection_null.visibility = View.VISIBLE
            rv_collection_list.visibility = View.GONE
        } else {
            tv_collection_null.visibility = View.GONE
            rv_collection_list.visibility = View.VISIBLE
        }
    }

    override fun showCancel(baseBean: BaseBean) {
        mAdapter.deletePositionData(deletePosition)
    }
}

