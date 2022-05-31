package io.hongting.websockettest.decoder;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hongting.websockettest.model.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * @author hongting
 * @create 2022 05 30 8:57 PM
 */
public class MessageDecoder implements Decoder.Text<Message>{

    @Override
    public Message decode(String s) throws DecodeException {
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = null;
        try {
            message = objectMapper.readValue(s, Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return message;

    }

    @Override
    public boolean willDecode(String s) {
        return (s!=null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
