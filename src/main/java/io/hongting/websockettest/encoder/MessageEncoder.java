package io.hongting.websockettest.encoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hongting.websockettest.model.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


/**
 * @author hongting
 * @create 2022 05 30 8:46 PM
 */
public class MessageEncoder implements Encoder.Text<Message>{

    @Override
    public String encode(Message message) throws EncodeException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = "";
        try{
            s = objectMapper.writeValueAsString(message);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return s;
    }



    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
