package com.xptschool.parent.ui.album;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.LocalFile;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.imgloader.AlbumViewPager;
import com.xptschool.parent.view.imgloader.MatrixImageView;

import java.util.List;

import butterknife.BindView;

public class LocalAlbumDetailActivity extends BaseActivity implements MatrixImageView.OnSingleTapListener {


    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.pagerview)
    FrameLayout pagerContainer;
    @BindView(R.id.albumviewpager)
    AlbumViewPager viewpager;

    private List<LocalFile> currentFolder = null;
    private AlbumViewPager.LocalViewPagerAdapter viewPagerAdapter;
    private AlbumGridViewAdapter adapter;
    private String folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album_detail);

        folder = getIntent().getExtras().getString(ExtraKey.LOCAL_FOLDER_NAME);
        setTitle(folder);
        initView();
    }

    private void initView() {
        adapter = new AlbumGridViewAdapter(LocalAlbumDetailActivity.this, new AlbumGridViewAdapter.AlbumGridViewClickListener() {
            @Override
            public void onCheckBoxChecked(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    if (LocalImageHelper.getInstance().getCheckedItems().contains((LocalFile) compoundButton.getTag())) {
                        LocalImageHelper.getInstance().removeCheckedItem((LocalFile) compoundButton.getTag());
                    }
                } else {
                    if (!LocalImageHelper.getInstance().getCheckedItems().contains((LocalFile) compoundButton.getTag())) {
                        if (LocalImageHelper.getInstance().getCheckedItems().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                            Toast.makeText(LocalAlbumDetailActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                            compoundButton.setChecked(false);
                            return;
                        }
                        LocalImageHelper.getInstance().insertCheckedItem((LocalFile) compoundButton.getTag());
                    }
                }
                setTopCompleteParcent();
            }

            @Override
            public void onImageViewClicked(LocalFile localFile, int position) {
                showViewPager(position);
            }
        });
        gridView.setAdapter(adapter);

        currentFolder = LocalImageHelper.getInstance().getFolder(folder);

        adapter.reloadPaths(currentFolder);
        if (viewPagerAdapter != null) {
            viewPagerAdapter.reloadPath(currentFolder);
        }

        setTopCompleteParcent();

        LocalImageHelper.getInstance().setResultOk(false);

        setBtnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalImageHelper.getInstance().setResultOk(true);
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

    private void setTopCompleteParcent() {
        List<LocalFile> checkedItems = LocalImageHelper.getInstance().getCheckedItems();
        //设置当前选中数量
        if (checkedItems.size() > 0) {
            String completePercent = getString(R.string.complete_percent, checkedItems.size(), LocalImageHelper.getInstance().getMaxChoiceSize());
            setBtnRight(completePercent);
        }
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }

    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() == View.VISIBLE) {
            hideViewPager();
        } else {
            super.onBackPressed();
        }
    }

    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
        if (viewPagerAdapter == null) {
            viewPagerAdapter = viewpager.new LocalViewPagerAdapter(currentFolder);
            viewpager.setAdapter(viewPagerAdapter);
        }
        viewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(300);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    private void hideViewPager() {
        pagerContainer.setVisibility(View.GONE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
        ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }

}
