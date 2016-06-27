package com.mistyrian.library.event;

/**
 * Created by mistyrain on 6/27/16.
 */

public class BaseReceiveEvent<T> {

    public static final int Flag_Success = 1;
    public static final int Flag_Fail = 2;


    protected int flag;
    protected T respMsg;
    protected int requestType;

    public int getFlag() {
        return flag;
    }

    public T getRespMsg() {
        return respMsg;
    }

    public int getRequestType() {
        return requestType;
    }


    public BaseReceiveEvent(int flag,T message){
        this.flag=flag;
        this.respMsg=message;
    }

}
