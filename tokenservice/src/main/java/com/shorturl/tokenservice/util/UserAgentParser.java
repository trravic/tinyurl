package com.shorturl.tokenservice.util;

import lombok.AllArgsConstructor;
import lombok.Data;

public class UserAgentParser {

    @Data
    @AllArgsConstructor
    public static class ParsedUserAgent {
        private String deviceType;
        private String browser;
        private String operatingSystem;
    }

    public static ParsedUserAgent parse(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return new ParsedUserAgent("UNKNOWN", "UNKNOWN", "UNKNOWN");
        }

        String ua = userAgent.toLowerCase();

        // Detect bots
        if (isBot(ua)) {
            return new ParsedUserAgent("BOT", "BOT", detectOS(ua));
        }

        // Detect device type
        String deviceType = detectDeviceType(ua);

        // Detect browser
        String browser = detectBrowser(ua);

        // Detect OS
        String os = detectOS(ua);

        return new ParsedUserAgent(deviceType, browser, os);
    }

    private static boolean isBot(String ua) {
        String[] botKeywords = {
                "bot", "crawler", "spider", "scraper", "curl", "wget",
                "python", "java", "apache", "headless"
        };

        for (String keyword : botKeywords) {
            if (ua.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static String detectDeviceType(String ua) {
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "MOBILE";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }

    private static String detectBrowser(String ua) {
        if (ua.contains("edg")) {
            return "Edge";
        } else if (ua.contains("chrome") && !ua.contains("edg")) {
            return "Chrome";
        } else if (ua.contains("firefox")) {
            return "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("opera") || ua.contains("opr")) {
            return "Opera";
        } else if (ua.contains("msie") || ua.contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Other";
        }
    }

    private static String detectOS(String ua) {
        if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac os") || ua.contains("macos")) {
            return "macOS";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        } else if (ua.contains("linux")) {
            return "Linux";
        } else {
            return "Other";
        }
    }
}