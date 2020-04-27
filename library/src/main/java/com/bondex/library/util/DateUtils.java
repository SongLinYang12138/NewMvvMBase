package com.bondex.library.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.bondex.library.R;


/**
 * date: 2019/6/19
 * Author: ysl
 * description:
 */
public class DateUtils {

    private static Dialog dialog = null;
    private static String date = "";
    private static DateCallBack mycallMack = null;

    @SuppressLint("NewApi")
    public static void showDate(Context context, DateCallBack callBack) {


        mycallMack = callBack;
        if (dialog == null) {


            dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog);

            View view = LayoutInflater.from(context).inflate(R.layout.dailog_date_layout, null);
            dialog.setContentView(view);

            final DatePicker datePicker = view.findViewById(R.id.dailog_date);
            Button btConfirm = view.findViewById(R.id.date_confirm);
            Button btCancel = view.findViewById(R.id.date_cancel);

            dialog.setCanceledOnTouchOutside(true);

            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mycallMack != null) {
                        mycallMack.dataCallBack(date);
                    }
                    dialog.hide();
                }
            });
            btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String year = String.format("%04d", datePicker.getYear());
                    String mon = String.format("%02d", datePicker.getMonth() + 1);
                    String day = String.format("%02d", datePicker.getDayOfMonth());
                    date = year + "-" + mon + "-" + day;


                    if  (mycallMack != null) {
                        mycallMack.dataCallBack(date);
                    }
                    dialog.hide();
                }
            });

            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();

            lp.width = (int) (CommonUtils.getScreenW(context) * 0.9);
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            if (!dialog.isShowing()) {
                dialog.show();
            }
        } else {

            if (!dialog.isShowing()) {
                dialog.show();
            }
        }


    }

}
