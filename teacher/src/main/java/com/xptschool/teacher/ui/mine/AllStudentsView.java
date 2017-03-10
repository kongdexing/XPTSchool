package com.xptschool.teacher.ui.mine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanStudent;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;

import java.util.List;

/**
 * Created by dexing on 2017/1/3.
 * No1
 */

public class AllStudentsView extends LinearLayout {

    private TextView txtClassName;
    private GridView grdvStudent;
    private ProgressBar progress;
    private StudentAdapter adapter;

    public AllStudentsView(Context context) {
        this(context, null);
    }

    public AllStudentsView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_all_student, this, true);
        txtClassName = (TextView) view.findViewById(R.id.txtClassName);
        grdvStudent = (GridView) view.findViewById(R.id.grdvStudent);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        adapter = new StudentAdapter(getContext());
        grdvStudent.setAdapter(adapter);
    }

    public void getStudentByClassId(BeanClass beanClass) {
        if (beanClass == null)
            return;

        txtClassName.setText(beanClass.getG_name() + beanClass.getC_name());

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyStudent_QUERY, new VolleyHttpParamsEntity()
                        .addParam("c_id", beanClass.getC_id())
                        .addParam("g_id", beanClass.getG_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyStudent_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (progress != null) {
                            progress.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanStudent> listStudent = gson.fromJson(volleyHttpResult.getData().toString(), new TypeToken<List<BeanStudent>>() {
                                    }.getType());
                                    adapter.loadStudent(listStudent);
                                } catch (Exception ex) {
                                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(getContext(), volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void setGrdvStudentOnClickListener(StudentAdapter.MyGridViewClickListener listener){
        adapter.setMyGridViewClickListener(listener);
    }

}
