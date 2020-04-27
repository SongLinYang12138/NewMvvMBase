/*
 * Copyright (c) 2017. Yang HongFei
 */

package com.bondex.library.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tools {


    private static final String NUMBER = "[^0-9]";
    private static final String DEMICAL = "[^0-9.,]";
    private static final String TAX = "[^0-9.,Be]";

    private static final String NUMBER_LETTER = "[^0-9A-Z]";
    private static final String CN_NUMBER_LETTER = "[^0-9\\u4E00-\\u9FA5a-zA-Z]";

    public static String getDigit(String s, int digit) {
        if (!isEmpty(s)) {
            s = s.replace(" ", "");
            if (getFromRegex(s, NUMBER).length() != digit) {
                return null;
            }
            Pattern p = Pattern.compile("[0-9]{" + digit + "}$");
            Matcher m = p.matcher(s);
            while (m.find()) {
                return m.group();
            }
        }
        return null;
    }

    public static boolean isSearch(String s, String regEx) {
        if (isEmpty(s.toString())) {
            return false;
        }
        s = s.replace(" ", "");
        if (isEmpty(s)) {
            return false;
        }
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static String getCurrentTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);

    }

    public static String getDate(long time_milles) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time_milles);
        return format.format(date);
    }


    public static String getDateAndTime(long time_milles) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time_milles);
        return format.format(date);
    }

    public static long getTimeMilles(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = format.parse(date);
            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (long) 0;
    }


    public static int getAccuracy(String s) {

        if (isEmpty(s) || s.indexOf(".") == -1) {
            return 0;
        }
        return s.length() - 1 - s.indexOf(".");
    }

    public static String getFromRegex(String s, String regEx) {

        if (isEmpty(s)) {
            return "";
        }
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        s = m.replaceAll("").trim();
        return s;
    }

    public static String getPercent(String s) {
        if (isEmpty(s)) {
            return "";
        }
        String regEx = "[^0-9.%]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        s = m.replaceAll("").trim();
        return s;
    }

    public static void addContact(Context context, String name, String tel) {

        if (isEmpty(tel)) {
            return;
        }
        int index = tel.indexOf("/");
        if (index != -1) {
            tel = tel.substring(0, index);
        }
        if (isEmpty(tel)) {
            return;
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(tel);
        tel = m.replaceAll("").trim();
        if (isEmpty(tel)) {
            return;
        }
        Intent addIntent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
        addIntent.setType("vnd.android.cursor.dir/person");
        addIntent.setType("vnd.android.cursor.dir/contact");
        addIntent.setType("vnd.android.cursor.dir/raw_contact");
        addIntent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, tel);

        context.startActivity(addIntent);
    }

    public static boolean isPlateNum(String num) {
        if (!isEmpty(num)) {
            num = num.replace(" ", "");
            Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{4}[a-zA-Z_0-9_\\u4e00-\\u9fa5]$");
            Matcher m = p.matcher(num);
            return m.matches();
        }
        return false;

    }

    public static String getAirPrimaryCheckCode(String num) {
        if (isEmpty(num)) {
            return "0";
        }
        String temp = num.replaceAll(" ", "").replaceAll("-", "");
        if (temp.length() < 10) {
            return "0";
        }
        temp = temp.substring(3);
        BigDecimal data = new BigDecimal(temp);
        BigDecimal x = data.divide(new BigDecimal("7"), 2, RoundingMode.HALF_UP);
        BigDecimal y = data.divide(new BigDecimal("7"), 4, RoundingMode.HALF_UP);
        y = y.subtract(new BigDecimal("0.49")).setScale(0, RoundingMode.HALF_UP);
        return x.subtract(y).multiply(new BigDecimal("7")).setScale(0, RoundingMode.HALF_UP).toString();

    }


    public static void phoneCall(Context context, String num) {
        if (isEmpty(num)) {
            return;
        }
        int index = num.indexOf("/");
        if (index != -1) {
            num = num.substring(0, index);
        }
        if (isEmpty(num)) {
            return;
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(num);
        num = m.replaceAll("").trim();
        if (isEmpty(num)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);

        Uri data = Uri.parse("tel:" + num);

        intent.setData(data);

        context.startActivity(intent);


    }

    protected static char[] encodeHex(byte[] data) {
        final char[] toDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }

    public static String getFileMD5(String path) {
        if (isEmpty(path)) {
            return null;
        }
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;


        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            digest = MessageDigest.getInstance("MD5");

            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
            return new String(encodeHex(digest.digest()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;

        }
    }

    public static boolean isEmpty(String code) {
        return TextUtils.isEmpty(code) || "null".equalsIgnoreCase(code);
    }

    public static int parseInt(String code) {
        if (isEmpty(code)) {
            return 0;
        }
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static float parseFloat(String code) {
        if (isEmpty(code)) {
            return -1;
        }
        try {
            return Float.parseFloat(code);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String parseString(double code) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(code);

    }

    public static String parseString(int code) {
        DecimalFormat df = new DecimalFormat("#");
        return df.format(code);

    }

    public static double parseDouble(String code) {
        if (isEmpty(code)) {
            return 0;
        }
        String regEx = "[^0-9-+.E]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(code);
        code = m.replaceAll("").trim();
        if (isEmpty(code)) {
            return 0;
        }
        try {
            return Double.parseDouble(code);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double parsePercentDouble(String code) {

        if (isEmpty(code)) {
            return 0;
        }
        try {
            return parseDouble(code) * 0.01;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int parseColor(String code) {
        if (isEmpty(code)) {
            return Color.parseColor("#203A93");

        }
        try {
            return Color.parseColor(code);
        } catch (IllegalArgumentException e) {
            return Color.parseColor("#203A93");
        }
    }

    public static void limitMoneyInput(final EditText editText) {

        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                10)});
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                StringBuffer sb = new StringBuffer(dest.toString());
                //构造输入后结果，考虑到复制粘贴的情况
                String result = sb.replace(dstart, dend, source.toString()).toString();
                CharSequence output = dest.subSequence(dstart, dend);
                if (result.length() > 1 && result.charAt(0) == '0' && result.charAt(1) != '.') {
                    //如果首位是0而第二位不是小数点，说明数字非法，不允许输入
                    return output;
                }
                int index = result.toString().indexOf(".");
                if (index != -1) {
                    //包含小数部分需要同时校验整数部分和小数部分
                    if (result.substring(index).length() >= 4 || result.substring(0, index).length() >= 10) {
                        return output;
                    }
                } else {
                    //不包含小数只校验整数部分
                    if (result.length() >= 10) {
                        return output;
                    }
                }

                return null;
            }
        }});
    }

    /**
     * 判断手机号格式
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneIlegal(String phone) {
        String s = null;
        if (!isEmpty(phone)) {
            s = phone.replace(" ", "");
            Pattern p = Pattern.compile("^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$");
            Matcher m = p.matcher(s);
            return m.matches();
        }
        return false;

    }

    public static String getTaxNum(String s) {
        if (isEmpty(s)) {
            return null;
        }
        s = s.replace(" ", "");
        int count = 0;
        for (char c : s.toCharArray()) {
            switch (c) {
                case '校':
                case '验':
                case '码':
                    count++;
                    break;
                default:
                    break;
            }
        }
        if (count != 0) {
            return null;
        }
        s = getFromRegex(s, NUMBER_LETTER);
        if (isEmpty(s)) {
            return null;
        }
        if (s.length() <= 20 && s.length() >= 15) {
            Pattern p = Pattern.compile("^[A-Z0-9]{2}[0-9]{6}[A-Z0-9]{9,10}$|^(\\d{6}[0-9A-Z]{9})$");
            Matcher m = p.matcher(s);
            while (m.find()) {
                return m.group();
            }
        }
        return null;

    }

    /**
     * 判断邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmailLegal(String email) {
        String s = null;
        if (!isEmpty(email)) {
            s = email.replace(" ", "");
            String str = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            Pattern p = Pattern.compile(str);
            Matcher m = p.matcher(s);
            return m.matches();
        }
        return false;
    }


    public static void openAlbum(Activity activity) {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            activity.startActivityForResult(i, 1);
        } catch (ActivityNotFoundException e) {

        }
    }


//    public static void sharePicture(String path, Activity activity) {
//        if (isEmpty(path)) {
//            return;
//        }
//        File file = new File(path);
//        Uri photoURI;
//        Intent it = new Intent(Intent.ACTION_SEND);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            photoURI = FileProvider.getUriForFile(activity, Constant.FILE_PROVIDER, file);
//            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        } else {
//            photoURI = Uri.fromFile(file);
//        }
//        it.putExtra(Intent.EXTRA_STREAM, photoURI);
//        it.setType("image/*");
//        activity.startActivity(it);
//    }

//    public static void openNougatFile(String path, Activity activity) {
//        if (isEmpty(path)) {
//            return;
//        }
//        File file = new File(path);
//        Uri photoURI = FileProvider.getUriForFile(activity, Constant.FILE_PROVIDER, file);
//        Intent it = new Intent(Intent.ACTION_VIEW);
//        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        it.setDataAndType(photoURI, "image/*");
//        activity.startActivity(it);
//    }


    /**
     * 保留两位小数
     *
     * @param s
     * @return
     */
    public static String formatMoney(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }
        double num = parseDouble(s);
        NumberFormat formater = new DecimalFormat("#.##");
        try {
            return formater.format(num);
        } catch (ArithmeticException e) {
            return "";
        }
    }


    public static String getTax(String words) {
        if (!isEmpty(words)) {
            words = getFromRegex(words, TAX);
            words = words.replace("B", "8");
            words = words.replace("e", "8");
            Pattern p = Pattern.compile("^[0-9]{1,8}[,.][0-9]{1,2}$");
            Matcher m = p.matcher(words);
            while (m.find()) {
                return m.group().replace(",", ".");
            }
        }
        return null;

    }

    public static String getDate(String words) {
        if (!isEmpty(words)) {
            words = words.replace(" ", "");
            Pattern p = Pattern.compile("[0-9]{4}年[0-9]{2}月[0-9]{2}");
            Matcher m = p.matcher(words);
            while (m.find()) {
                if (m.group() != null) {
                    return m.group().replace("年", "-").replace("月", "-");
                }
            }
        }
        return null;

    }

    public static String getmc(String words) {

        if (!isEmpty(words) && (words.contains("有限") || words.contains("有限公司") || words.contains("有限公") || words.contains("有限责任公司") || words.contains("股份有限公司") || words.contains("股份有限责任公司") || words.contains("公司"))) {
            int index = words.indexOf(':');
            if (index != -1 && index + 1 < words.length()) {
                words = words.substring(index + 1);
            }
            words = getFromRegex(words, CN_NUMBER_LETTER);
            if (words.length() > 5) {
                return words;
            }
        }

        return null;
    }


    public static String getdzdh(String words) {
        if (isEmpty(words)) {
            return null;
        }
        int count = 0;
        for (char c : words.toCharArray()) {
            switch (c) {
                case '省':
                case '市':
                case '路':
                case '县':
                case '区':
                case '街':
                case '栋':
                case '座':
                case '楼':
                case '号':
                case '镇':
                    count++;
                    break;
                default:
                    break;
            }
            if (words.contains("胡同")) {
                count++;
            }
            if (count > 2) {
                String regEx = "[地址电话]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(words);
                words = m.replaceAll("").trim();
                words = getFromRegex(words, CN_NUMBER_LETTER);
                if (words.length() > 5) {
                    return words;
                }
            }
        }
        return null;
    }

    public static String getyhzh(String words) {
        if (isEmpty(words)) {
            return null;
        }
        if (words.contains("银行") && (words.contains("分行") || words.contains("支行") || words.contains("总行"))) {
            int index = words.indexOf(':');
            if (index != -1 && index + 1 < words.length()) {
                words = words.substring(index + 1);
            }
            words = getFromRegex(words, CN_NUMBER_LETTER);
            if (words.length() > 5) {
                return words;
            }
        }
        return null;

    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }



    public static List<String> checkPort(String s) {
        if (isEmpty(s)) {
            return null;
        }
        List<String> list = Arrays.asList(s.split("-"));
        if (list.size() == 3) {
            return list;
        }
        return null;
    }

    public static List<String> checkPack(String s) {
        if (isEmpty(s)) {
            return null;
        }
        List<String> list = Arrays.asList(s.split("-"));
        if (list.size() == 3) {
            return list;
        }
        return null;
    }


}
