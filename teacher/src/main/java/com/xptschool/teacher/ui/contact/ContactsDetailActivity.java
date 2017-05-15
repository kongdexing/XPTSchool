package com.xptschool.teacher.ui.contact;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.view.CircularImageView;
import com.android.widget.view.KenBurnsView;
import com.jph.takephoto.app.TakePhoto;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.DividerItemDecoration;
import com.xptschool.teacher.adapter.WrapContentLinearLayoutManager;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.ContactStudent;
import com.xptschool.teacher.model.ContactTeacher;
import com.xptschool.teacher.ui.album.AlbumActivity;
import com.xptschool.teacher.ui.chat.ChatActivity;
import com.xptschool.teacher.ui.main.BaseActivity;
import com.xptschool.teacher.view.AlbumSourceView;

import java.io.File;
import java.util.List;

import butterknife.BindView;

public class ContactsDetailActivity extends BaseActivity {

    @BindView(R.id.imgHead)
    CircularImageView imgHead;

    @BindView(R.id.txtAreaName)
    TextView txtAreaName;

    @BindView(R.id.txtSchoolName)
    TextView txtSchoolName;

    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.rlTeacherPhone)
    RelativeLayout rlTeacherPhone;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtBirth_date)
    TextView txtBirth_date;

    @BindView(R.id.llTeacher)
    LinearLayout llTeacher;

    @BindView(R.id.llStudent)
    LinearLayout llStudent;

    @BindView(R.id.imgSex)
    ImageView imgSex;

    @BindView(R.id.llInfoBg)
    LinearLayout llInfoBg;

    @BindView(R.id.txtAge)
    TextView txtAge;


    @BindView(R.id.txtParentCount)
    TextView txtParentCount;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    private PopupWindow picPopup;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_detial);

        KenBurnsView mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_student, R.drawable.bg_student);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(ExtraKey.CONTACT_TYPE);
            if (type.equals(ExtraKey.CONTACT_TEACHER)) {
                llTeacher.setVisibility(View.VISIBLE);
                llStudent.setVisibility(View.GONE);
                setTeacherInfo((ContactTeacher) bundle.get(ExtraKey.CONTACT));
            } else {
                llTeacher.setVisibility(View.GONE);
                llStudent.setVisibility(View.VISIBLE);
                setStudentInfo((ContactStudent) bundle.get(ExtraKey.CONTACT));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type.equals(ExtraKey.CONTACT_STUDENT)) {
            recycleView.getAdapter().notifyDataSetChanged();
        }
    }

    private void setTeacherInfo(final ContactTeacher teacher) {
        if (teacher == null) {
            return;
        }
        setTitle(teacher.getName());

        if (teacher.getSex().equals("1")) {
            imgHead.setImageResource(R.drawable.teacher_man);
        } else {
            imgHead.setImageResource(R.drawable.teacher_woman);
        }
        txtName.setText(teacher.getName());
        txtAreaName.setText(teacher.getArea_name());
        txtSchoolName.setText(teacher.getSchool_name());
        txtPhone.setText(teacher.getPhone());
        rlTeacherPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(teacher.getPhone());
            }
        });
    }

    private void setStudentInfo(ContactStudent student) {
        if (student == null) {
            return;
        }
        setTitle(student.getStu_name());

        if (student.getSex().equals("1")) {
            imgHead.setImageResource(R.drawable.student_boy);
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_boy);
            imgSex.setBackgroundResource(R.drawable.male_w);
        } else {
            imgHead.setImageResource(R.drawable.student_girl);
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_girl);
            imgSex.setBackgroundResource(R.drawable.female_w);
        }

        txtAge.setText(student.getAge() + "岁");

        txtName.setText(student.getStu_name());
        txtBirth_date.setText(student.getBirth_date());
        txtClassName.setText(student.getG_name() + student.getC_name());
        List<ContactParent> parents = student.getParent();
        if (parents == null || parents.size() == 0) {
            txtParentCount.setText("无家长通讯信息");
            return;
        }

        ContactParentAdapter adapter = new ContactParentAdapter(this);
        adapter.refreshDate(parents);
        adapter.setPhoneClickListener(new ContactParentAdapter.OnParentPhoneClickListener() {
            @Override
            public void onPhoneClickListener(ContactParent parent) {
                showBottomView(recycleView, parent);
            }
        });

        recycleView.setHasFixedSize(true);
        final WrapContentLinearLayoutManager mLayoutManager = new WrapContentLinearLayoutManager(this);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.line_dotted));
        recycleView.setAdapter(adapter);
    }

    public void showBottomView(View view, final ContactParent parent) {
        //选择相片来源
        if (picPopup == null) {
            BottomChatView albumSourceView = new BottomChatView(ContactsDetailActivity.this);
            albumSourceView.setOnBottomChatClickListener(new BottomChatView.OnBottomChatClickListener() {
                @Override
                public void onCallClick() {
                    call(parent.getPhone());
                    picPopup.dismiss();
                }

                @Override
                public void onChatClick() {
                    Intent intent = new Intent(ContactsDetailActivity.this, ChatActivity.class);
                    intent.putExtra(ExtraKey.CHAT_PARENT, parent);
                    startActivity(intent);
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

    private void call(String phone) {
        Log.i(TAG, "call: ");
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            Log.i(TAG, "toCallPhone: " + ex.getMessage());
            Toast.makeText(this, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
        }
    }

}
