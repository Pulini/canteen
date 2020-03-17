package com.pzx.canteen.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * File Name : AccountSuitModel
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：账套model
 */
@XStreamAlias("NewDataSet")
public class AccountSuitModel {
    //<NewDataSet>
//    <DbHelperTable>
//        <FAccountSuitID>1</FAccountSuitID>
//        <Code>01|金帝集团股份有限公司</Code>
//        <FAccountSuitName>金帝集团股份有限公司</FAccountSuitName>
//    </DbHelperTable>


    @XStreamImplicit()
    List<table> DbHelperTable = new ArrayList<table>();


    public List<table> getDbHelperTable() {
        return DbHelperTable;
    }

    public void setDbHelperTable(List<table> dbHelperTable) {
        DbHelperTable = dbHelperTable;
    }

    public static class table {
        String FAccountSuitID;
        String Code;
        String FAccountSuitName;

        @Override
        public String toString() {
            return FAccountSuitName;
        }

        public String getFAccountSuitID() {
            return FAccountSuitID;
        }

        public void setFAccountSuitID(String FAccountSuitID) {
            this.FAccountSuitID = FAccountSuitID;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getFAccountSuitName() {
            return FAccountSuitName;
        }

        public void setFAccountSuitName(String FAccountSuitName) {
            this.FAccountSuitName = FAccountSuitName;
        }
    }
}
