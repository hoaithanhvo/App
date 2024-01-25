package com.example.nidecsnipeit.utils;

import java.util.HashMap;
import java.util.Map;

public class FullNameConvert {
    private static final Map<String, String> keyFullNameMap = new HashMap<>();

    static {
        keyFullNameMap.put("serial", "Serial Number");
        keyFullNameMap.put("model", "Model");
        keyFullNameMap.put("name", "Asset Name");
        keyFullNameMap.put("location", "Location");
        keyFullNameMap.put("assigned_to", "Assigned To");
        keyFullNameMap.put("notes", "Notes");
    }

    public static String getFullName(String key) {
        String fullName = keyFullNameMap.getOrDefault(key, key);
        if (fullName != null) {
            return fullName;
        }
        return key.replace("_", " ").toUpperCase();
    }
}
