package com.edwip.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanKick {
    public static Map<String, String> banKick(String message) {
        System.out.println(message);
        Map<String, String> list = new HashMap<>();
        Pattern kickPattern = Pattern.compile(
                "^(\\S+) was kicked by (\\S+) for '(.*?)'.$" +
                        "|^(\\S+) was kicked by (\\S+).$"
        );
        Pattern kickRobloxPattern = Pattern.compile(
                "^(\\S+) has been kicked from Roblox by (\\S+) for (.*?).$"
        );
        Pattern banPattern = Pattern.compile(
                "^(\\S+) temp IP-banned (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) tempbanned (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) banned (\\S+) for '(.*?)'$"
        );
        Pattern banRobloxPattern = Pattern.compile(
                "^(\\S+) has been banned from Roblox by (\\S+) for (.*?).$"
        );
        Pattern mutePattern = Pattern.compile(
                "^(\\S+) tempmuted (\\S+) for (.*?) for '(.*?)'$" +
                        "|^(\\S+) muted (\\S+) for '(.*?)'$"
        );
        Pattern warnPattern = Pattern.compile(
                "^(\\S+) warned (\\S+) for '(.*?)'$"
        );
        Pattern unbanPattern = Pattern.compile(
                "^(\\S+) unbanned (\\S+) for '(.*?)'$"
        );
        Pattern unmutePattern = Pattern.compile(
                "^(\\S+) ummuted (\\S+) for '(.*?)'$"
        );
        list.put("Type", "Unknown");
        list.put("Platform", "Unknown");
        list.put("User", "Unknown");
        list.put("By", "Unknown");
        list.put("Reason", "No reason specified.");
        list.put("Duration", "None");
        if (kickPattern.matcher(message).find()) {
            Matcher matcher = kickPattern.matcher(message);
            matcher.find();
            list.put("Type", "Kick");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(3) != null) {
                // kicked with reason
                list.put("User", matcher.group(1));
                list.put("By", matcher.group(2));
                list.put("Reason", matcher.group(3));
            } else if (matcher.group(4) != null) {
                // kicked without reason
                list.put("User", matcher.group(4));
                list.put("By", matcher.group(5));
            }
        } else if (kickRobloxPattern.matcher(message).find()) {
            Matcher matcher = kickRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Kick");
            list.put("Platform", "Roblox");
            list.put("User", matcher.group(1));
            list.put("By", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (banPattern.matcher(message).find()) {
            Matcher matcher = banPattern.matcher(message);
            matcher.find();
            list.put("Type", "Ban");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(4) != null) {
                // temp IP-banned
                list.put("By", matcher.group(1));
                list.put("User", matcher.group(2));
                list.put("Duration", matcher.group(3));
                list.put("Reason", matcher.group(4));
            } else if (matcher.group(5) != null && matcher.group(8) != null) {
                // tempbanned
                list.put("By", matcher.group(5));
                list.put("User", matcher.group(6));
                list.put("Duration", matcher.group(7));
                list.put("Reason", matcher.group(8));
            } else if (matcher.group(9) != null && matcher.group(11) != null) {
                // banned
                list.put("By", matcher.group(9));
                list.put("User", matcher.group(10));
                list.put("Duration", "Permanent");
                list.put("Reason", matcher.group(11));
            }
        } else if (banRobloxPattern.matcher(message).find()) {
            Matcher matcher = banRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Ban");
            list.put("Platform", "Roblox");
            list.put("User", matcher.group(1));
            list.put("By", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (warnPattern.matcher(message).find()) {
            Matcher matcher = warnPattern.matcher(message);
            matcher.find();
            list.put("Type", "Warn");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (mutePattern.matcher(message).find()) {
            Matcher matcher = mutePattern.matcher(message);
            matcher.find();
            list.put("Type", "Mute");
            list.put("Platform", "Minecraft");
            if (matcher.group(1) != null && matcher.group(4) != null) {
                // temp muted
                list.put("By", matcher.group(1));
                list.put("User", matcher.group(2));
                list.put("Duration", matcher.group(3));
                list.put("Reason", matcher.group(4));
            } else if (matcher.group(5) != null && matcher.group(7) != null) {
                // muted
                list.put("By", matcher.group(5));
                list.put("User", matcher.group(6));
                list.put("Duration", "Permanent");
                list.put("Reason", matcher.group(7));
            }
        } else if (unbanPattern.matcher(message).find()) {
            Matcher matcher = unbanPattern.matcher(message);
            matcher.find();
            list.put("Type", "Unban");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else if (unmutePattern.matcher(message).find()) {
            Matcher matcher = banRobloxPattern.matcher(message);
            matcher.find();
            list.put("Type", "Unmute");
            list.put("Platform", "Minecraft");
            list.put("By", matcher.group(1));
            list.put("User", matcher.group(2));
            list.put("Reason", matcher.group(3));
        } else {
            System.out.println("List not matched.");
        }
        return list;
    }

}
