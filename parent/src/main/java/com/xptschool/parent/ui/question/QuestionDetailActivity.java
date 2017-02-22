package com.xptschool.parent.ui.question;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanQuestion;
import com.xptschool.parent.bean.BeanQuestionTalk;
import com.xptschool.parent.bean.MessageSendStatus;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class QuestionDetailActivity extends BaseActivity {

    @BindView(R.id.txtQstTitle)
    TextView txtQstTitle;

    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.edtContent)
    EditText edtContent;
    private QuestionDetailAdapter adapter = null;
    private BeanQuestion mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mQuestion = bundle.getParcelable(ExtraKey.QUESTION);
            if (mQuestion != null) {
                setTitle(mQuestion.getReceiver_name());
                txtQstTitle.setText("标题：" + mQuestion.getTitle());
                getQuestionTalk();
            }
        }
    }

    private void initView() {
        adapter = new QuestionDetailAdapter(this);
        listview.setAdapter(adapter);
    }

    @OnClick(R.id.btnSend)
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                String msg = edtContent.getText().toString();
                if (msg.isEmpty()) {
                    return;
                }
//                adapter.insertChat(msg);
                edtContent.setText("");
                BeanQuestionTalk answer = new BeanQuestionTalk();
                answer.setSendStatus(MessageSendStatus.SENDING);
                BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
                if (parent != null) {
                    answer.setSender_id(parent.getU_id());
                    answer.setSender_sex(parent.getSex());
                }
                answer.setCreate_time((new Date()) + "");
                answer.setContent(msg);
                adapter.insertChat(answer);
                sendAnswer(answer);
                break;
        }
    }

    private void getQuestionTalk() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_VIEW,
                new VolleyHttpParamsEntity()
                        .addParam("id", mQuestion.getId())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_VIEW)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanQuestionTalk> questions = gson.fromJson(httpResult.getData().toString(), new TypeToken<List<BeanQuestionTalk>>() {
                                    }.getType());
                                    for (int i = 0; i < questions.size(); i++) {
                                        questions.get(i).setReceiver_sex(mQuestion.getReceiver_sex());
                                    }
                                    adapter.refreshData(questions);
                                    listview.setSelection(adapter.getCount() - 1);
                                } catch (Exception ex) {
                                    Toast.makeText(QuestionDetailActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(QuestionDetailActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QuestionDetailActivity.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendAnswer(final BeanQuestionTalk answer) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_ADD,
                new VolleyHttpParamsEntity()
                        .addParam("id", mQuestion.getId())
                        .addParam("content", answer.getContent())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_ADD)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        answer.setSendStatus(MessageSendStatus.SENDING);
                        adapter.updateChat(answer);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                answer.setSendStatus(MessageSendStatus.SUCCESS);
                                adapter.updateChat(answer);
                                break;
                            default:
                                answer.setSendStatus(MessageSendStatus.FAILED);
                                adapter.updateChat(answer);
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        answer.setSendStatus(MessageSendStatus.FAILED);
                        adapter.updateChat(answer);
                    }
                });
    }
}
