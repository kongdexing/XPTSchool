package com.xptschool.parent.ui.fence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.xptschool.parent.R;
import com.xptschool.parent.common.ActivityResultCode;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.push.UpushTokenHelper;
import com.xptschool.parent.ui.fragment.MineFragment;
import com.xptschool.parent.ui.leave.LeaveDetailActivity;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.main.LoginActivity;
import com.xptschool.parent.view.CustomDialog;
import com.xptschool.parent.view.CustomEditDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 绘制围栏
 */
public class FenceDrawActivity extends BaseActivity {

    @BindView(R.id.llRevoke)
    LinearLayout llRevoke;
    @BindView(R.id.mapView)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> listLatLng = new ArrayList<>();
    private BeanStudent student = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_draw);
        setTitle("添加围栏");
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        student = (BeanStudent) bundle.getSerializable(ExtraKey.STUDENT_ID);
        if (student == null) {
            return;
        }

        setTxtRight("添加");
        setTextRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listLatLng.size() < 3) {
                    Toast.makeText(FenceDrawActivity.this, "需要选中三个点才能确定一个区域", Toast.LENGTH_SHORT).show();
                    return;
                }

                CustomEditDialog dialog = new CustomEditDialog(FenceDrawActivity.this);
                dialog.setTitle("围栏名称");
                dialog.setHintEdit(R.string.input_fence_name);
                dialog.setAlertDialogClickListener(new CustomEditDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick(String value) {
                        if (value.isEmpty()) {
                            Toast.makeText(FenceDrawActivity.this, R.string.input_fence_name, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addFence(value);
                    }
                });
            }
        });
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
                listLatLng.add(point);
                drawFence();
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                listLatLng.add(poi.getPosition());
                drawFence();
                return false;
            }
        });
    }

    @OnClick({R.id.llRevoke})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.llRevoke:
                if (listLatLng.size() > 0) {
                    listLatLng.remove(listLatLng.size() - 1);
                    drawFence();
                }
                break;
        }
    }

    private void drawFence() {
        mBaiduMap.clear();
        if (listLatLng.size() == 0) {
            return;
        }
        if (listLatLng.size() > 2) {
            OverlayOptions ooPolygon = new PolygonOptions().points(listLatLng)
                    .stroke(new Stroke(5, getResources().getColor(R.color.colorPrimary)))
                    .fillColor(getResources().getColor(R.color.transparent_gray_map));
            mBaiduMap.addOverlay(ooPolygon);
        } else if (listLatLng.size() == 2) {
            OverlayOptions ooPolyline = new PolylineOptions().width(6)
                    .color(getResources().getColor(R.color.colorPrimary)).points(listLatLng);
            mBaiduMap.addOverlay(ooPolyline);
        } else {
            OverlayOptions ooDot = new DotOptions().center(listLatLng.get(0)).radius(6)
                    .color(getResources().getColor(R.color.colorPrimary));
            mBaiduMap.addOverlay(ooDot);
        }
    }

    private void addFence(String name) {
        if (student == null) {
            Toast.makeText(this, "无学生信息", Toast.LENGTH_SHORT).show();
            return;
        }
        String point = "";
        for (int i = 0; i < listLatLng.size(); i++) {
            point += listLatLng.get(i).longitude + "," + listLatLng.get(i).latitude + ",";
        }
        if (point.length() > 0) {
            point = point.substring(0, point.length() - 1);
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_addStudentFence,
                new VolleyHttpParamsEntity()
                        .addParam("stu_id", student.getStu_id())
                        .addParam("s_id", student.getS_id())
                        .addParam("a_id", student.getA_id())
                        .addParam("g_id", student.getG_id())
                        .addParam("c_id", student.getC_id())
                        .addParam("point", point)
                        .addParam("name", name)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_addStudentFence)), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_loading_cn);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(FenceDrawActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            setResult(1);
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                        Toast.makeText(FenceDrawActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
