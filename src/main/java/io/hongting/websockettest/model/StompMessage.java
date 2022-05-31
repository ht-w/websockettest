package io.hongting.websockettest.model;

/**
 * @author hongting
 * @create 2022 05 31 12:53 PM
 */
public class StompMessage {

    private String username;

    private String msgContent;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
}
