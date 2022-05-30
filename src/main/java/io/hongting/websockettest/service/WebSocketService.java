package io.hongting.websockettest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hongting
 * @create 2022 05 26 1:46 PM
 */


@ServerEndpoint("/ws")
@Service
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    private  Map<String, String> user = new HashMap<>();

    public Map<String, String> getUser() {
        return user;
    }



    {
        user.put("hongting",  "康庭");
        user.put("visitor1", "visitor1");
        user.put("visitor2", "visitor2");
        user.put("visitor3", "visitor3");
    }


    //number of online user
    private static AtomicInteger onlineUser = new AtomicInteger(0);

    // to store connected websocket client
    private static CopyOnWriteArraySet<WebSocketService> webSocketServices  = new CopyOnWriteArraySet();

    private Session session;


    public Session getSession() {
        return session;
    }


    //This method is called when the client connects to the server for the first time
    @OnOpen
    public void onOpen (Session session){
        String name = user.get(session.getUserPrincipal().getName());
        this.session = session;
        webSocketServices.add(this);
        incrementOnlineUser();
        logger.info("New websocket connection, current number of online user: {}", onlineUser.get());
        webSocketServices.parallelStream().forEach(ws -> {
            try {
                sendMessage(ws.getSession(), String.format("%s join!", name));
            } catch (IOException e) {
                logger.error("Message exception:", e);
            }
        });

    }

    //This method is called when the client sends a message
    @OnMessage
    public void onMessage (String message, Session session){
        logger.info("Message from the client side: {}", message);
        String name = user.get(session.getUserPrincipal().getName());
        logger.info ("{} sent a message : {}", name, message);
        webSocketServices.parallelStream().forEach(ws -> {
            try {
                ws.sendMessage(ws.getSession(), String.format("%s:%s", name, message));
            } catch (IOException e) {
                logger.error("Message exception:", e);
            }
        });
    }

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @OnMessage
    public void onMessage (ByteBuffer byteBuffer, boolean complete, Session session ){
        String name = user.get(session.getUserPrincipal().getName());
        try {
            buffer.write(byteBuffer.array());
            if (complete){
                FileOutputStream fos = null;
                try{
                    fos = new FileOutputStream( "D:\\audiotest\\2.mp3");
                    fos.write(buffer.toByteArray());
                }finally{
                    if (fos!=null){
                        fos.flush();
                        fos.close();
                    }
                }

                webSocketServices.parallelStream().forEach(ws -> {
                    final ByteBuffer sendData = ByteBuffer.allocate(buffer.toByteArray().length);
                    sendData.put(buffer.toByteArray());
                    sendData.rewind();
                    try{
                        ws.sendAudio(ws.getSession(), sendData);
                    } catch (IOException e) {
                        logger.error("Audio message exception:", e);
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //streaming
//    @OnMessage
//    public void onMessage(InputStream inputStream) {
//        try {
//            byte[] buff = new byte[inputStream.available()];
//            inputStream.read(buff, 0, inputStream.available());
//            webSocketServices.parallelStream().forEach(ws -> {
//                try {
//                    ws.sendAudio(ws.getSession(), ByteBuffer.wrap(buff));
//                } catch (IOException e) {
//                    logger.error("Message exception:", e);
//                }
//            });
//        } catch (Exception e) {
//            logger.error("Message exception:", e);
//        }
//    }



    //This method is called when the client disconnects
    @OnClose
    public void onClose (Session session){
        String name = session.getUserPrincipal().getName();
        logger.info("{} quit the connection", name);
        webSocketServices.remove(this);
        decrementOnlineUser();
        logger.info("current number of online user: {}", onlineUser.get());
        webSocketServices.parallelStream().forEach(ws -> {
            try {
                sendMessage(ws.getSession(), String.format("%s left!", name));
            } catch (IOException e) {
                logger.error("Message exception:", e);
            }
        });
    }


    //This method is called when an error occurs
    @OnError
    public void onError(Session session, Throwable t) {
        if (this.session!=null && this.session.isOpen()){
            logger.error("Websocket connection error:", t);
            webSocketServices.remove(this);
            try {
                session.close();
            } catch (IOException e) {
                logger.error("websocket connection exception{}", this.toString(), e);
            }
        }
    }


    private void  incrementOnlineUser(){
        onlineUser.incrementAndGet();
    }

    private void decrementOnlineUser(){
        onlineUser.decrementAndGet();
    }

    private void sendMessage(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    private void sendAudio(Session session, ByteBuffer byteBuffer) throws IOException {
        session.getAsyncRemote().sendBinary(byteBuffer, sendResult -> logger.info(String.valueOf(sendResult.isOK())));
    }



}
