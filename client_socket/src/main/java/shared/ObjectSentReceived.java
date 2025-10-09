/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.io.Serializable;

/**
 *
 * @author hn235
 */
public class ObjectSentReceived implements Serializable{
    private String type;
    private Object obj;

    public ObjectSentReceived() {
    }

    public ObjectSentReceived(String type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "ObjectSentReceived{" + "type=" + type + ", obj=" + obj + '}';
    }
    
}
