package com.xptschool.parent.ui.album;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ImageUtils;
import com.xptschool.parent.common.LocalFile;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.common.StringUtils;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.AlbumSourceView;
import com.xptschool.parent.view.imgloader.AlbumViewPager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Administrator on 2016/10/29.
 */
@RuntimePermissions
public class AlbumActivity extends BaseActivity {

    public ScrollView mScrollView;
    private PopupWindow picPopup;
    public AlbumGridAdapter myPicGridAdapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        AlbumActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void toLocalAlbum() {
        Log.i(TAG, "toLocalAlbum: ");
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onStorageDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForStorage(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        request.proceed();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onStorageNeverAskAgain() {
        Log.i(TAG, "onStorageNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_storage_never_askagain, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                refreshGridView();
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                String cameraPath = LocalImageHelper.getInstance().getCameraImgPath();
                if (StringUtils.isEmpty(cameraPath)) {
                    return;
                }
                File file = new File(cameraPath);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    LocalFile localFile = new LocalFile();
                    localFile.setThumbnailUri(uri.toString());
                    localFile.setOriginalUri(uri.toString());
                    localFile.setOrientation(getBitmapDegree(cameraPath));
                    localFile.setParentFileName(StringUtils.getParentPath(cameraPath));
                    LocalImageHelper.getInstance().getCheckedItems().add(localFile);
                    LocalImageHelper.getInstance().setResultOk(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshGridView();
                        }
                    }, 300);
                } else {
                    Toast.makeText(this, R.string.image_loadfailed, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void showAlbumSource(View view) {
        //选择相片来源
        if (picPopup == null) {
            AlbumSourceView albumSourceView = new AlbumSourceView(AlbumActivity.this);
            albumSourceView.setOnAlbumSourceClickListener(new AlbumSourceView.OnAlbumSourceClickListener() {
                @Override
                public void onAlbumClick() {
                    AlbumActivityPermissionsDispatcher.toLocalAlbumWithCheck(AlbumActivity.this);
                    picPopup.dismiss();
                }

                @Override
                public void onCameraClick() {
                    if (LocalImageHelper.getInstance().getCheckedItems().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                        Toast.makeText(AlbumActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //  拍照后保存图片的绝对路径
                        String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
                        File file = new File(cameraPath);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intent,
                                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                    } catch (Exception ex) {
                        Toast.makeText(AlbumActivity.this, R.string.toast_camera_failed, Toast.LENGTH_SHORT).show();
                    }
                    picPopup.dismiss();
                }

                @Override
                public void onBack() {
                    picPopup.dismiss();
                }
            });
            picPopup = new PopupWindow(albumSourceView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            picPopup.setTouchable(true);
            picPopup.setBackgroundDrawable(new ColorDrawable());
            picPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
        }
        backgroundAlpha(0.5f);
        picPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    //显示大图pager
    public void showViewPager(AlbumViewPager albumviewpager, int index) {
        if (albumviewpager == null)
            return;
        albumviewpager.setVisibility(View.VISIBLE);
        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(LocalImageHelper.getInstance().getCheckedItems()));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //显示大图pager
    public void showNetImgViewPager(AlbumViewPager albumviewpager, List<String> imgUris, int index) {
        if (albumviewpager == null)
            return;
        albumviewpager.setVisibility(View.VISIBLE);
        albumviewpager.setAdapter(albumviewpager.new NetViewPagerAdapter(imgUris));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //关闭大图显示
    public void hideViewPager(AlbumViewPager albumviewpager) {
        if (albumviewpager == null)
            return;
        albumviewpager.setVisibility(View.GONE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    /**
     * 读取图片的旋转的角度，还是三星的问题，需要根据图片的旋转角度正确显示
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private void refreshGridView() {
        if (LocalImageHelper.getInstance().isResultOk()) {
            LocalImageHelper.getInstance().setResultOk(false);
            List<String> imgs = new ArrayList<>();
            List<LocalFile> checkedItems = LocalImageHelper.getInstance().getCheckedItems();
            for (LocalFile file : checkedItems) {
                imgs.add(file.getOriginalUri());
            }
            myPicGridAdapter.reloadPicture(imgs);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalImageHelper.getInstance().clear();
    }

}
