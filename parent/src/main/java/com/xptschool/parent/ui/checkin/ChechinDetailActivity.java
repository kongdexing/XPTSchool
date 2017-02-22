package com.xptschool.parent.ui.checkin;

import android.os.Bundle;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

public class ChechinDetailActivity extends BaseActivity {

//    PagingRecyclerView pagingRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chechin_detail);

//        pagingRecyclerView = (PagingRecyclerView) findViewById(R.id.paging_recycler_view);
//        pagingRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(ChechinDetailActivity.this, "setDefaultOnRefreshListener", Toast.LENGTH_SHORT).show();
//                pagingRecyclerView.setRefreshing(false);
//            }
//        });
//
//        final StudentCheckinAdapter adapter = new StudentCheckinAdapter();
//        pagingRecyclerView.setAdapter(adapter);
//
//        pagingRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
//            @Override
//            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
//
////                Toast.makeText(ChechinDetailActivity.this, "setOnLoadMoreListener " + itemsCount, Toast.LENGTH_SHORT).show();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        adapter.insert();
//                    }
//                }, 1000);
//            }
//        });

    }
}
