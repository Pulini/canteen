package com.pzx.canteen.model;

/**
 * File Name : ResultModel
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：接口返回实体model
 */
public class ResultModel {
    boolean IsSuccess;

    String Name;

    Object data;

    public Object getData() {
        return this.data;
    }

    public String getName() {
        return this.Name;
    }

    public boolean isSuccess() {
        return this.IsSuccess;
    }

    public void setData(Object paramObject) {
        this.data = paramObject;
    }

    public void setName(String paramString) {
        this.Name = paramString;
    }

    public void setSuccess(boolean paramBoolean) {
        this.IsSuccess = paramBoolean;
    }
}
