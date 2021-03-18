package com.pzx.canteen.newApp

/**
 * File Name : UserModel
 * Created by : PanZX on 2020/04/15
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 用户数据
 */
data class UserBean(
    val DefaultStockName: String = "",//默认仓库名
    val DefaultStockID: Int = 0,//默认仓库ID
    val DepartmentID: Int = 0,//部门ID
    val DepartmentName: String = "",//部门名称
    val DiningRoomID: Int = 0,//默认就餐食堂ID
    val DutyID: Int = 0,//职务ID
    val EmpID: Int = 0,//员工ID
    val Factory: String = "",//厂区
    val IsAppAutoLock: Int = 0,//APP自动上锁
    val Name: String = "",//姓名
    val Number: String = "",//员工工号
    val OrganizeID: Int = 0,//组织ID
    val PassWord: String = "",//密码
    val PicUrl: String = "",//照片路径
    val Position: String = "",//职称
    val QuickLoginType: Int = 0,//快速登录类型
    val SAPFactory: String = "",//SAP厂区
    val SAPLineNumber: String = "",//SAP线别编号
    val SAPRole: String = "",//SAP角色
    val Sex: String = "",//性别
    val Token: String = "",//账号验证码
    val UserID: Int = 0//用户ID
)