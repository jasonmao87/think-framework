package com.think.tcp2.core.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/16 14:40
 * @description : TODO
 */
public class PayloadListenerManager {

    private static List<TcpPayloadEventListener> listeners = new ArrayList<>();


    public static List<TcpPayloadEventListener> getListeners() {
        return listeners;
    }

//    public static Iterator<TcpPayloadEventListener> getExecuteIterator(){
//        return listeners.iterator();
//    }


    public static final void  addFirst(TcpPayloadEventListener listener){
        if(listener!=null) {
            listeners.add(0, listener);
        }
    }

    public static final void addLast(TcpPayloadEventListener listener){
        if(listener!=null){
            listeners.add(listener);
        }
    }

    public static final void clearListeners(){
        listeners.clear();
    }


    public static final void removeFirst(){
        if(listeners.size()>0) {
            listeners.remove(0);
        }
    }

    public static final void removeLast(){
        synchronized (listeners) {
            if(listeners.isEmpty() == false ){
                int index = listeners.size() -1 ;
                listeners.remove(index);
            }
        }

    }


}
