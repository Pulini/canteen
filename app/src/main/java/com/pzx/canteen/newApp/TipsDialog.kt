package com.pzx.canteen.newApp

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import com.pzx.canteen.R


/**
 * File Name : TipsDialog
 * Created by : PanZX on 2020/06/03
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：提示弹窗
 */
class TipsDialog(context: Context) : Dialog(context, R.style.MessageDialog) {

    private val tvMsg: TextView by lazy { findViewById<TextView>(R.id.tv_msg) }
    private val tvConfirm: TextView by lazy { findViewById<TextView>(R.id.tv_confirm) }

    init {
        setContentView(R.layout.dialog_tips)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        val lp: WindowManager.LayoutParams? = window?.attributes
        lp?.width = dp2px(260f)
        window?.attributes = lp
        tvConfirm.setOnClickListener {
            dismiss()
        }
    }

    inline fun show(func: TipsDialog.() -> Unit): TipsDialog = apply {
        this.func()
        this.show()
    }

    fun message(msg: String): TipsDialog = apply {
        tvMsg.text = msg
    }

    fun confirm(click: () -> Unit = { dismiss() }): TipsDialog = apply {
        tvConfirm.setOnClickListener {
            click.invoke()
        }
    }
}