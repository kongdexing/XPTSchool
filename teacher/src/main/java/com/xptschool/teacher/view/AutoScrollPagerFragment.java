package com.xptschool.teacher.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.widget.autoviewpager.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.teacher.R;

public class AutoScrollPagerFragment extends Fragment {

    private String[] imgs = {"http://h.hiphotos.baidu.com/image/w%3D1920%3Bcrop%3D0%2C0%2C1920%2C1080/sign=fed1392e952bd40742c7d7f449b9a532/e4dde71190ef76c6501a5c2d9f16fdfaae5167e8.jpg",
            "http://a.hiphotos.baidu.com/image/w%3D1920%3Bcrop%3D0%2C0%2C1920%2C1080/sign=25d477ebe51190ef01fb96d6fc2ba675/503d269759ee3d6df51a20cd41166d224e4adedc.jpg",
            "http://c.hiphotos.baidu.com/image/w%3D1920%3Bcrop%3D0%2C0%2C1920%2C1080/sign=70d2b81e60d0f703e6b291d53aca6a5e/0ff41bd5ad6eddc4ab1b5af23bdbb6fd5266333f.jpg"};
    private AutoScrollViewPager pager;
    private CirclePageIndicator indicator;
    private MyPagerAdapter adapter;

    public AutoScrollPagerFragment() {
        // Required empty public constructor
        Log.i("reback", "AutoScrollPagerFragment: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auto_scroll_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (AutoScrollViewPager) getView().findViewById(R.id.scroll_pager);
        final TextView title = (TextView) getView().findViewById(R.id.title);
        indicator = (CirclePageIndicator) getView().findViewById(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setSnap(true);

        pager.setScrollFactgor(5);
        pager.setOffscreenPageLimit(4);
        pager.startAutoScroll(2000);
    }

    public void setDatas() {
        adapter.reloadData(imgs);
    }

    class MyPagerAdapter extends PagerAdapter {

        private String[] topImgs = new String[]{};

        public void reloadData(String[] tops) {
            topImgs = tops;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return topImgs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setBackground(getResources().getDrawable(R.drawable.pictures_no));
//            ImageLoader.getInstance().displayImage(imgs[position], new ImageViewAware(view), CommonUtil.getDefaultImageLoaderOption());
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    ;

}
