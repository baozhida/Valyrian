package com.xxx.model.utils.basic;

import java.util.ArrayList;
import java.util.List;

public class Results {

    List<Result> resultItems = new ArrayList<Result>();

    boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<Result> getResultItems() {
        return resultItems;
    }

    public void setResultItems(List<Result> resultItems) {
        this.resultItems = resultItems;
    }

    public void setResultByItems() {
        result = true;
        List<Result> resultItems = this.getResultItems();
        for (Result items : resultItems) {
            result = result && items.isResult();
        }
    }

    @Override 
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Result result : resultItems) {
        	if (!result.isResult()) {
                sb.append("参数 :").append(result.getKey()).append(" 校验结果: ").append(result.isResult()).append(" 预期: ")
                        .append(result.getExpect()).append(" 实际: ").append(result.getActual());
        	}
        }
        return sb.toString();
    }
}
