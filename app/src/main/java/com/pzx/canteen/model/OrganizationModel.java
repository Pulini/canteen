package com.pzx.canteen.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * File Name : OrganizationModel
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：组织model
 */
@XStreamAlias("NewDataSet")
public class OrganizationModel {
    //<NewDataSet>
    //    <DbHelperTable>
    //        <FItemID>1</FItemID>
    //        <Code>01.01|金帝集团股份有限公司</Code>
    //        <FName>金帝集团股份有限公司</FName>
    //        <FNumber>01.01</FNumber>
    //        <FAdminOrganizeID>1</FAdminOrganizeID>
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
        String FItemID;
        String Code;
        String FName;
        String FNumber;
        String FAdminOrganizeID;

        public String getFItemID() {
            return FItemID;
        }

        public void setFItemID(String FItemID) {
            this.FItemID = FItemID;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getFName() {
            return FName;
        }

        public void setFName(String FName) {
            this.FName = FName;
        }

        public String getFNumber() {
            return FNumber;
        }

        public void setFNumber(String FNumber) {
            this.FNumber = FNumber;
        }

        public String getFAdminOrganizeID() {
            return FAdminOrganizeID;
        }

        public void setFAdminOrganizeID(String FAdminOrganizeID) {
            this.FAdminOrganizeID = FAdminOrganizeID;
        }

        @Override
        public String toString() {
            return FName;
        }
    }
}