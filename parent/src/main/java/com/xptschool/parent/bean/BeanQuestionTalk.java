package com.xptschool.parent.bean;

/**
 * Created by Administrator on 2016/10/29.
 */
//问题
public class BeanQuestionTalk {

    private String id;
    private String sender_id;
    private String sender_sex;
    private String receiver_id;
    private String receiver_sex;
    private String title;
    private String content;
    private String create_time;
    private MessageSendStatus sendStatus = MessageSendStatus.SUCCESS;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_sex() {
        return sender_sex;
    }

    public void setSender_sex(String sender_sex) {
        this.sender_sex = sender_sex;
    }

    public String getReceiver_sex() {
        return receiver_sex;
    }

    public void setReceiver_sex(String receiver_sex) {
        this.receiver_sex = receiver_sex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public MessageSendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageSendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }
}
