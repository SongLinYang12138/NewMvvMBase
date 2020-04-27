/*
 * Copyright (c) 2017. Yang HongFei
 */

package com.bondex.library.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by YangHongfei on 2016/12/20.
 * 手机号，身份证，银行卡输入框内容格式化（空格分离），
 * 使用方法：FormatTextUtils.formatText(edittext,FormatTextUtils.BANK_CARD);
 * 手机号和银行卡号会根据传入的type来设置InputType和digist，无需在xml中设置
 * 身份证号，由于可能存在英文字符，在英文联想开启的情况下有bug，因此需要在xml中指定digist
 */

public class FormatTextUtils {
    /**
     * 银行卡类型
     */
    public static final int BANK_CARD = 1;
    /**
     * 手机号类型
     */
    public static final int PHONE = 2;
    /**
     * 身份证类型
     */
    public static final int ID_CARD = 3;
    //空运主单
    public static final int AIR_PRIMARY_NUM = 4;

    private static final String TAG = FormatTextUtils.class.getSimpleName();


    /**
     * @param editText
     * @param type
     */
    public static void formatText(final EditText editText, final int type) {
        if (editText == null) {
            Log.e(TAG, "Edit text is null");
            return;
        }
        final int max;
        switch (type) {
            case AIR_PRIMARY_NUM:
                max = 12;
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789 -"));
                break;
            case PHONE:
                max = 13;
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789 "));

                break;
            case ID_CARD:
                max = 21;
                //开启英文联想情况下动态设置digit会导致无法输入x
                break;
            case BANK_CARD:
                max = 23;
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789 "));
                break;
            default:
                max = 24;
                break;
        }

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                max)});
        final TextWatcher textWatcher = new TextWatcher() {
            final StringBuffer buffer = new StringBuffer();
            int lastLength = -1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //lastLength保存变化前光标后有效数字的长度
                if (count == 1 && after == 0 && (s.charAt(start) == ' ' || s.charAt(start) == '-')) {
                    lastLength = -1;
                } else if (editText.getSelectionEnd() < s.length()) {
                    lastLength = s.subSequence(editText.getSelectionEnd(), s.length()).toString().replace(" ", "").replace("-", "").length();
                } else {
                    lastLength = -1;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buffer.setLength(0);
                buffer.append(s.toString().replace(" ", "").replace("-", ""));
                int index = 0;
                int spaceCount = 0;
                while (index < buffer.length()) { // 插入所有空格
                    //格式化在这里实现
                    switch (type) {
                        case AIR_PRIMARY_NUM:

                            if (index == 3
                                    ) {
                                buffer.insert(index, '-');
                                spaceCount++;
                            } else if ((index > 7) && ((index - 3) % (spaceCount << 2) == spaceCount)) {
                                buffer.insert(index, ' ');
                                spaceCount++;
                            }

                            break;
                        case BANK_CARD:
                            if (index > 3
                                    && (index % ((spaceCount + 1) << 2) == spaceCount)) {
                                buffer.insert(index, ' ');
                                spaceCount++;
                            }
                            break;
                        case PHONE:
                            if (index == 3
                                    || ((index > 7) && ((index - 3) % (spaceCount << 2) == spaceCount))) {
                                buffer.insert(index, ' ');
                                spaceCount++;
                            }
                            break;
                        case ID_CARD:
                            if (index == 6
                                    || ((index > 10) && ((index - 6) % (spaceCount << 2) == spaceCount))) {
                                buffer.insert(index, ' ');
                                spaceCount++;
                            }
                            break;
                        default:
                            if (index > 3
                                    && (index % ((spaceCount + 1) << 2) == spaceCount)) {
                                buffer.insert(index, ' ');
                                spaceCount++;
                            }
                            break;
                    }
                    index++;
                }
                editText.removeTextChangedListener(this);
                //重置内容时防止不必要的监听
                editText.getEditableText().replace(0, s.length(), buffer.toString());
                editText.addTextChangedListener(this);

                if (lastLength != -1) {
                    int selection = editText.getSelectionEnd();
                    int loc = s.length();
                    //变化前后光标后的有效数字位数不变，因此从末尾向前搜索，找到该位数为止
                    if (loc > 0) {
                        while (lastLength > 0) {
                            loc--;
                            if (loc > -1 && s.charAt(loc) != ' ' && s.charAt(loc) != '-') {
                                lastLength--;
                            }
                        }
                    }
                    if (selection != loc) {
                        editText.setSelection(loc);
                    }
                    //如果删除后左边是空格，光标左移一位
                    if (count == 0 && loc > 1 && (s.charAt(loc - 1) == ' ' || s.charAt(loc - 1) == '-')) {
                        editText.setSelection(loc - 1);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };
        editText.removeTextChangedListener(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public static void formatText(final TextView textView, final int type) {
        if (textView == null || TextUtils.isEmpty(textView.getText().toString())) {
            return;
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append(textView.getText().toString().trim().replace(" ", ""));
        int index = 0;
        int spaceCount = 0;
        while (index < buffer.length()) { // 插入所有空格
            //格式化在这里实现
            switch (type) {
                case BANK_CARD:
                    if (index > 3
                            && (index % ((spaceCount + 1) << 2) == spaceCount)) {
                        buffer.insert(index, ' ');
                        spaceCount++;
                    }
                    break;
                case PHONE:
                    if (index == 3
                            || ((index > 7) && ((index - 3) % (spaceCount << 2) == spaceCount))) {
                        buffer.insert(index, ' ');
                        spaceCount++;
                    }
                    break;
                case AIR_PRIMARY_NUM:
                    if (index == 3
                            || ((index > 7) && ((index - 3) % (spaceCount << 2) == spaceCount))) {
                        buffer.insert(index, ' ');
                        spaceCount++;
                    }
                    break;
                case ID_CARD:
                    if (index == 6
                            || ((index > 10) && ((index - 6) % (spaceCount << 2) == spaceCount))) {
                        buffer.insert(index, ' ');
                        spaceCount++;
                    }
                    break;
                default:
                    if (index > 3
                            && (index % ((spaceCount + 1) << 2) == spaceCount)) {
                        buffer.insert(index, ' ');
                        spaceCount++;
                    }
                    break;
            }
            index++;
        }
        textView.setText(buffer.toString());
    }
}
