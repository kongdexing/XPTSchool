package com.xptschool.teacher.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
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
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;

import java.util.List;

public class StudentFragment extends DialogFragment {

    private GridView grdvStudent;
    private StudentAdapter adapter;
    private ProgressBar progress;

    public StudentFragment() {
        // Required empty public constructor
    }

    public void setClass(BeanClass beanClass) {
        getStudentByClassId(beanClass);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        grdvStudent = (GridView) view.findViewById(R.id.grdvStudent);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        adapter = new StudentAdapter(getContext());
        grdvStudent.setAdapter(adapter);
        adapter.setMyGridViewClickListener(new StudentAdapter.MyGridViewClickListener() {
            @Override
            public void onGridViewItemClick(BeanStudent student) {
                Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
                intent.putExtra(ExtraKey.STUDENT_DETAIL, student);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    private void getStudentByClassId(BeanClass beanClass) {
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
                        super.onErrorResponse(volleyError);
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
