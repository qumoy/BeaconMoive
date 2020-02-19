package com.beacon.moive.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;


/**
 * Author Qumoy
 * Create Date 2019/7/29
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class DialogUtil {

    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param pContent            提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static Dialog showSelectDialog(Context context, String title, String pContent, String pLeftBtnStr,
                                          String pRightBtnStr,
                                          final DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(pContent)
                .setPositiveButton(pRightBtnStr, (dialog12, which) -> {
                    dialogClickListener.confirm();
                    dialog12.dismiss();
                })
                .setNegativeButton(pLeftBtnStr, (dialog1, which) -> {
                    dialogClickListener.cancel();
                    dialog1.dismiss();
//                        return;
                })
                .create();
        return dialog;
    }


    public interface DialogClickListener {

        void confirm();

        void cancel();

    }

    public interface BeaconDialogClickListener {

        void change(int id, String str);

        void add(Dialog view);

        void back(Dialog view);
    }

}
