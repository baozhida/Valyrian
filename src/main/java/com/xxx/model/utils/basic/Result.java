package com.xxx.model.utils.basic;

import com.xxx.utils.convert.ConvertUtil;

public class Result {
    private boolean result;
    
    private Object key;
    
    private Object expect;
    
    private Object actual;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getExpect() {
        return expect;
    }

    public void setExpect(Object expect) {
        this.expect = expect;
    }

    public Object getActual() {
        return actual;
    }

    public void setActual(Object actual) {
        this.actual = actual;
    }

    @Override 
    public String toString() {
        return ConvertUtil.toJson(this);
    }
}
