package com.pzx.canteen.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.pzx.canteen.LoginActivity;
import com.pzx.canteen.R;

/**
 * File Name : DialogUtil
 * Created by : PanZX on 2020/03/17
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：
 */
public class DialogUtil {

    /**
     * 账套列表获取失败弹窗
     * @param context
     * @param msg
     * @param click
     */
    public static void showASListFailedDialog(Context context, String msg, DialogInterface.OnClickListener click) {
        new AlertDialog
                .Builder(context)
                .setIcon(R.mipmap.logo)
                .setTitle(context.getString(R.string.error))
                .setCancelable(false)
                .setMessage(String.format(context.getString(R.string.failed_to_get_account_suit_list), msg))
                .setPositiveButton(context.getString(R.string.retry), click)
                .create()
                .show();
    }

    /**
     * 组织列表获取失败弹窗
     * @param context
     * @param msg
     * @param click
     */
    public static void showOListFailedDialog(Context context, String msg, DialogInterface.OnClickListener click) {
        new AlertDialog
                .Builder(context)
                .setIcon(R.mipmap.logo)
                .setTitle(context.getString(R.string.error))
                .setCancelable(false)
                .setMessage(String.format(context.getString(R.string.failed_to_get_organization_list), msg))
                .setPositiveButton(context.getString(R.string.retry), click)
                .create()
                .show();
    }

    /**
     * 登录失败弹窗
     * @param context
     * @param msg
     * @param click
     */
    public static void showLoginFailedDialog(Context context, String msg, DialogInterface.OnClickListener click) {
        new AlertDialog
                .Builder(context)
                .setIcon(R.mipmap.logo)
                .setTitle(context.getString(R.string.error))
                .setCancelable(false)
                .setMessage(String.format(context.getString(R.string.login_failed), msg))
                .setPositiveButton(context.getString(R.string.confirm), click)
                .create()
                .show();

    }

    /**
     * 退出App弹窗
     * @param context
     */
    public static void showExitDialog(Context context) {
        new AlertDialog
                .Builder(context)
                .setIcon(R.mipmap.logo)
                .setTitle(context.getString(R.string.exit))
                .setCancelable(false)
                .setMessage(context.getString(R.string.exit_remind))
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }
     /**
     * 退出登录弹窗
     * @param activity
     */
    public static void showSignOutDialog(final Activity activity) {
        new AlertDialog
                .Builder(activity)
                .setIcon(R.mipmap.logo)
                .setTitle(activity.getString(R.string.sign_out))
                .setCancelable(false)
                .setMessage(activity.getString(R.string.sign_out_remind))
                .setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


}
