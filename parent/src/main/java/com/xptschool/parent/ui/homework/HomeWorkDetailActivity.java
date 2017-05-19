package com.xptschool.parent.ui.homework;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.widget.mygridview.MyGridView;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanHomeWork;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.ui.album.AlbumActivity;
import com.xptschool.parent.ui.album.AlbumGridAdapter;
import com.xptschool.parent.view.imgloader.AlbumViewPager;

import butterknife.BindView;

public class HomeWorkDetailActivity extends AlbumActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtSubject)
    TextView txtSubject;

    @BindView(R.id.gridview)
    MyGridView gridView;

    @BindView(R.id.llTeacher)
    LinearLayout llTeacher;

    @BindView(R.id.txtTeacher)
    TextView txtTeacher;

    @BindView(R.id.edtName)
    TextView edtName;

    @BindView(R.id.edtContent)
    TextView edtContent;

    @BindView(R.id.txtPushTime)
    TextView txtPushTime;

    @BindView(R.id.llCreateTime)
    LinearLayout llCreateTime;

    @BindView(R.id.txtCompleteTime)
    TextView txtCompleteTime;

    @BindView(R.id.albumviewpager)
    AlbumViewPager albumviewpager;

    private BeanHomeWork currentHomeWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_home_work);

        mScrollView = scrollView;

        setTitle(R.string.homework_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentHomeWork = bundle.getParcelable(ExtraKey.HOMEWORK_DETAIL);
        }
        initData();
    }

    private void initData() {
        myPicGridAdapter = new AlbumGridAdapter(this, new AlbumGridAdapter.MyGridViewClickListener() {
            @Override
            public void onGridViewItemClick(int position, String imgPath) {
                if (currentHomeWork != null) {
                    showNetImgViewPager(albumviewpager, currentHomeWork.getFile_path(), position);
                } else {
                    if (position == 0) {
//                        if (LocalImageHelper.getInstance().getCheckedItems().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
//                            Toast.makeText(HomeWorkDetailActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        showAlbumSource(albumviewpager);
                    } else {
                        showViewPager(albumviewpager, position - 1);
                    }
                }
            }
        });
        gridView.setAdapter(myPicGridAdapter);

        if (currentHomeWork != null) {
            edtName.setText(currentHomeWork.getName());
            txtPushTime.setText(currentHomeWork.getCreate_time());
            txtCompleteTime.setText(currentHomeWork.getFinish_time());
            txtTeacher.setText(currentHomeWork.getUser_name());
            edtContent.setText(currentHomeWork.getWork_content());
            myPicGridAdapter.initDate(currentHomeWork.getFile_path(), false);

            txtPushTime.setClickable(false);
            txtCompleteTime.setClickable(false);
            txtClassName.setText(currentHomeWork.getG_name() + currentHomeWork.getC_name());
            txtSubject.setText(currentHomeWork.getCrs_name());
        }

        initVoice(currentHomeWork);
    }

    @Override
    public void onBackPressed() {
        if (albumviewpager.getVisibility() == View.VISIBLE) {
            hideViewPager(albumviewpager);
        } else {
            super.onBackPressed();
        }
    }

}
