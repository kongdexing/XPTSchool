package com.xptschool.teacher.ui.main;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.DividerItemDecoration;
import com.xptschool.teacher.adapter.WrapContentLinearLayoutManager;
import com.xptschool.teacher.bean.ResultPage;

/**
 * Created by Administrator on 2016/11/1.
 */

public class BaseListActivity extends BaseActivity {

    public ResultPage resultPage = new ResultPage();
    private WrapContentLinearLayoutManager mLayoutManager;

    public void initRecyclerView(RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout) {
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new WrapContentLinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.line_dotted));
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
//        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                        .getDisplayMetrics()));
    }

    public WrapContentLinearLayoutManager getLayoutManager(){
        return mLayoutManager;
    }


}
