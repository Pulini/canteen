package com.pzx.canteen.newApp

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pzx.canteen.R

/**
 * File Name : LoadingDialog
 * Created by : PanZX on 2020/06/03
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：加载弹窗
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.MessageDialog) {


    init {
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        val lp: WindowManager.LayoutParams? = window?.attributes
        lp?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
    }

    fun show(msg: String?) {
        show()
        findViewById<TextView>(R.id.tv_msg).text=msg
    }
}