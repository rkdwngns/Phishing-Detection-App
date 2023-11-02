package com.example.message_detection;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsgDT {
    static String msg_scan(String nbr, String text) {

            if (adr_chk(nbr)) {
                return null;
            }
            if (Phone_num_filter(nbr))
                return "피싱번호";

            if (PPL_filter(text))
                return "광고";

            if (Url_filter(text))
                return "악성url";

            if (Short_Url(text))
                return "미확인 url";

            if (paymentfraud_filter(text))
                return "위장결제";

            if (phone_call(text))
                return "전화유도";
            return null;
    }

    private static boolean paymentfraud_filter(String text){
        Pattern phonePattern = Pattern.compile("\\d{2,4}-\\d{3,4}-?\\d{0,4}");
        Matcher matcher = phonePattern.matcher(text);
        String[] keywords = {"국외발신", "국제발신", "해외결제"};
        boolean foundKeyword = false;
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                foundKeyword = matcher.find();
                break;
            }
        }


        return foundKeyword;
    }

    private static boolean phone_call(String text){
        Pattern phonePattern = Pattern.compile("\\d{2,4}-\\d{3,4}-?\\d{0,4}");
        Matcher matcher = phonePattern.matcher(text);

        return matcher.find();
    }


    //광고 탐지
    private static boolean PPL_filter(String text) {
        String PPL = "(광고)";
        // 문자열에 "광고" 단어가 포함되어 있는지 확인
        boolean containsPPL = text.contains(PPL);

        return containsPPL;
    }

    private static boolean adr_chk(String nbr) {

        ArrayList<String> adrList = AdrData.getInstance().getArrayList();
        Log.d("AdressBook", "Name" + nbr);
        if (adrList.contains(nbr)) {
            Log.d("Notification Details", "주소록에 저장되어있음");
            return true;
        } else {
            Log.d("Notification Details", "주소록에 저장되어있지 않음");
            return false;
        }
    }

    private static boolean Phone_num_filter(String newPhoneNumber) {
        MsgAdapter msgAdapter = GlobalStore.getMsgAdpater();
        if (msgAdapter == null) {
            return false;
        }
        ArrayList<MessageData> messages = msgAdapter.getMessageList();
        Log.d("Phone_num_filter", "item " + msgAdapter.getCount());

        for(MessageData message : messages) {
            if(message.getType() == 2) {
                if (message.getTitle().equals(newPhoneNumber)) {
                    Log.d("Phone_num_filter", "동일 번호 존재");
                    return true; // 동일한 번호가 이미 존재하면 true 반환
                }
            }
        }
        return false;
    }

    //url탐지
    private static boolean Url_filter(String url) {
        // URL 패턴 정규식
        String urlPattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        // 정규식 패턴 객체 생성
        Pattern Url_pattern = Pattern.compile(urlPattern);

        // 문자열에 URL 패턴이 포함되어 있는지 확인
        boolean containsUrl = Url_pattern.matcher(url).find();

        return containsUrl;
    }
    private static boolean Short_Url(String Surl) {
        String SUrlPattern = "(bit\\.ly|kl\\.am|cli\\.gs|bc\\.vc|po\\.st|v\\.gd" +
                "|bkite\\.com|shorl\\.com|scrnch\\.me" +
                "|to\\.ly|adf\\.ly|x\\.co" +
                "|1url.com|migre.me" +
                "|su.pr" +
                "|smallurl.co|" +
                "cutt.us\\w*?\\/.*?[^/]|filoops.info\\w*?\\/.*?[^/]|shor7.com\\w*?\\/.*?[^/]" +
                "|yfrog.com\\w*?\\/.*?[^/]|tinyurl.com\\w*?\\/.*?[^/]|u.to\\w*?\\/.*?[^/]" +
                "|ow.ly\\w*?.*/[a-zA-Z0-9]+|[a-zA-Z0-9]+.ow.ly|r2me.com|r2.ly" +
                ")";
        Pattern pattern = Pattern.compile(SUrlPattern);
        Matcher matcher = pattern.matcher(Surl);

        return matcher.find();
    }
}
