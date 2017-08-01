package com.xptschool.parent.ui.cardset;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.view.CustomDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class CardSetBaseActivity extends ContractClickActivity {

    public Button btnSubmit;
    public static String CARD_SOS = "sos";
    public static String CARD_WHITELIST = "whitelist";
    public static String CARD_MONITER = "monitor";
    public String CardType = "";
    public BeanStudent currentStudent;
    public String spKey = "";

    @Override
    public void onChooseContractResult(String[] contacts) {
        super.onChooseContractResult(contacts);
        Intent intent = new Intent();
        if (CardType.equals(CARD_SOS)) {
            intent.setAction(BroadcastAction.SOS_CONTACTS);
            intent.putExtra("phone", contacts[1]);
        } else if (CardType.equals(CARD_WHITELIST)) {
            intent.setAction(BroadcastAction.WHITELIST_CONTACTS);
            intent.putExtra("name", contacts[0]);
            intent.putExtra("phone", contacts[1]);
        } else if (CardType.equals(CARD_MONITER)) {
            intent.setAction(BroadcastAction.WHITELIST_CONTACTS);
            intent.putExtra("name", contacts[0]);
            intent.putExtra("phone", contacts[1]);
        }
        sendBroadcast(intent);
    }

    public void setCardPhone(final String values) {
        sendPhoneNumber(values);
    }

    public void getCardPhone() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GetCard_Phone,
                new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.GetCard_Phone)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONArray jsonArray = new JSONArray(volleyHttpResult.getData().toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String type = object.getString("d_type");
                                        String value = object.getString("d_value");
                                        if (CardType.equals(type)) {
                                            if (CardType.equals(CARD_SOS)) {
                                                currentStudent.setSos(value);
                                            } else if (CardType.equals(CARD_WHITELIST)) {
                                                currentStudent.setWhitelist(value);
                                            } else if (CardType.equals(CARD_MONITER)) {
                                                currentStudent.setMonitor(value);
                                            }
                                            setViewData(value);
                                            GreenDaoHelper.getInstance().updateStudent(currentStudent);
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.i(TAG, "getCardPhone onResponse: " + ex.getMessage());
                                    Toast.makeText(CardSetBaseActivity.this, "获取学生卡号码失败", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    private void sendPhoneNumber(final String values) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.SetCard_Phone, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("typeFlag", CardType)
                        .addParam("s_id", currentStudent.getS_id())
                        .addParam("a_id", currentStudent.getA_id())
                        .addParam("g_id", currentStudent.getA_id())
                        .addParam("c_id", currentStudent.getC_id())
                        .addParam("imei", currentStudent.getImei_id())
                        .addParam(CardType, values)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.SetCard_Phone)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_loading_cn);
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(false);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        hideProgress();
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                //保存在本地
                                if (CardType.equals(CARD_SOS)) {
                                    currentStudent.setSos(values);
                                } else if (CardType.equals(CARD_WHITELIST)) {
                                    currentStudent.setWhitelist(values);
                                } else if (CardType.equals(CARD_MONITER)) {
                                    currentStudent.setMonitor(values);
                                }
                                SharedPreferencesUtil.saveData(CardSetBaseActivity.this, spKey, System.currentTimeMillis() + "");
                                GreenDaoHelper.getInstance().updateStudent(currentStudent);
                                break;
                            case HttpAction.FAILED:
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                                    final String phone = jsonObject.getString("phone");
//                                    String d_value = jsonObject.getString("d_value");
                                    final String content = jsonObject.getString("content");

                                    CustomDialog dialog = new CustomDialog(CardSetBaseActivity.this);
                                    dialog.setTitle(R.string.label_cardset);
                                    dialog.setMessage(R.string.label_card_sendsms);
                                    dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                                            intent.putExtra("sms_body", content);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (Exception ex) {
                                    if (volleyHttpResult.getInfo() != null) {
                                        Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            default:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgress();
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void setViewData(String value) {
        if (currentStudent != null) {
            String parentId = GreenDaoHelper.getInstance().getCurrentParent().getU_id();
            spKey = parentId + currentStudent.getStu_id() + CardType;
        }
        Log.i(TAG, "setViewData:  " + spKey);
    }

}
