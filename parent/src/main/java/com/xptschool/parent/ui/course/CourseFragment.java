package com.xptschool.parent.ui.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class CourseFragment extends Fragment {

    private static String TAG = CourseFragment.class.getSimpleName();
    private CourseAdapter adapter;
    private ProgressBar progress;
    private TextView txtClassName;
    private GridView gridview;
    private BeanStudent currentStudent;

    public CourseFragment() {
        // Required empty public constructor
    }

    public void setStudent(BeanStudent student) {
        currentStudent = student;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        gridview = (GridView) view.findViewById(R.id.gridview);
        txtClassName = (TextView) view.findViewById(R.id.txtClassName);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        adapter = new CourseAdapter(getContext(), gridview);
        gridview.setAdapter(adapter);

        txtClassName.setText(currentStudent.getG_name() + currentStudent.getC_name());
        getCourseByStudent(currentStudent);
        return view;
    }

    private void getCourseByStudent(BeanStudent student) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Timetable_QUERY, new VolleyHttpParamsEntity()
                        .addParam("c_id", student.getC_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Timetable_QUERY)),
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
                        //初始化空课程表
                        LinkedHashMap<String, LinkedHashMap<String, String>> linkedCourse = new LinkedHashMap<String, LinkedHashMap<String, String>>();
                        //节次
                        for (int i = 0; i < 8; i++) {
                            LinkedHashMap<String, String> courseName = new LinkedHashMap<String, String>();
                            //星期
                            for (int j = 0; j < 7; j++) {
                                courseName.put((j + 1) + "", "");
                            }
                            linkedCourse.put((i + 1) + "", courseName);
                        }

                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    HashMap<String, String> hashCourse = new HashMap<String, String>();
                                    JSONObject json = new JSONObject(volleyHttpResult.getData().toString());
                                    JSONObject allCourse = json.getJSONObject("course");
                                    Iterator itCourse = allCourse.keys();
                                    while (itCourse.hasNext()) {
                                        String key = itCourse.next().toString();
                                        String val = allCourse.getString(key);
                                        hashCourse.put(key, val);
                                    }

                                    Log.i(TAG, "empty linkedCourse size: " + linkedCourse.size());

                                    JSONObject table = json.getJSONObject("timeTable");
                                    Iterator itTable = table.keys();
                                    while (itTable.hasNext()) {
                                        try {
                                            String key = itTable.next().toString();
                                            JSONArray vals = table.getJSONArray(key);
                                            String[] keys = key.split("_");
                                            String section = keys[0];
                                            String week = keys[1];
                                            String courseId = vals.getString(0);
                                            linkedCourse.get(section).put(week, courseId.isEmpty() ? "" : hashCourse.get(courseId));
                                        } catch (Exception ex) {
                                            Log.i(TAG, "analyse course : " + ex.getMessage());
                                        }
                                    }
                                    Log.i(TAG, "adapter hashCode: " + adapter.hashCode());
                                    Log.i(TAG, "onResponse: linkedCourse size " + linkedCourse.size());
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                }
                                break;
                            default:
                                if (progress != null) {
                                    progress.setVisibility(View.GONE);
                                }
                                break;
                        }

                        adapter.loadDate(linkedCourse);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        //drawable empty table
                        //初始化空课程表
                        LinkedHashMap<String, LinkedHashMap<String, String>> linkedCourse = new LinkedHashMap<String, LinkedHashMap<String, String>>();
                        //节次
                        for (int i = 0; i < 8; i++) {
                            LinkedHashMap<String, String> courseName = new LinkedHashMap<String, String>();
                            //星期
                            for (int j = 0; j < 7; j++) {
                                courseName.put((j + 1) + "", "");
                            }
                            linkedCourse.put((i + 1) + "", courseName);
                        }
                        adapter.loadDate(linkedCourse);
                    }
                });
    }

}
