package com.example.nidecsnipeit.utility;

import java.util.HashMap;
import java.util.Map;

public class FullNameConvert {
    private static final Map<String, String> keyFullNameMap = new HashMap<>();

    static {
        keyFullNameMap.put("serial", "Serial Number");
        keyFullNameMap.put("model", "Model");
        keyFullNameMap.put("name", "Asset Name");
        keyFullNameMap.put("assigned_to", "Asset Assigned to");
        keyFullNameMap.put("location", "Location");
        keyFullNameMap.put("notes", "Notes");

        // Maintenance screen
        keyFullNameMap.put("asset_maintenance_type", "Maintenance type");
        keyFullNameMap.put("title", "Title");
        keyFullNameMap.put("supplier", "Supplier");
    }

    public static String getFullName(String key) {
        String fullName = keyFullNameMap.getOrDefault(key, key);
        if (fullName != null) {
            return fullName;
        }
        return key.replace("_", " ").toUpperCase();
    }

    public static String getKeyByFullName(String fullName) {
        for (Map.Entry<String, String> entry : keyFullNameMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(fullName)) {
                return entry.getKey();
            }
        }
        return fullName.toLowerCase().replace(" ", "_");
    }
}
