package com.scyh.applock.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scyh.applock.model.CommLockInfo;

public class DataUtil {

    public static List<CommLockInfo> clearRepeatCommLockInfo(List<CommLockInfo> lockInfos) {
        HashMap<String, CommLockInfo> hashMap = new HashMap<String,CommLockInfo>();
        for (CommLockInfo lockInfo : lockInfos) {
            if (!hashMap.containsKey(lockInfo.getPackageName())) {
                hashMap.put(lockInfo.getPackageName(), lockInfo);
            }
        }
        List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
        for (HashMap.Entry<String, CommLockInfo> entry : hashMap.entrySet()) {
            commLockInfos.add(entry.getValue());
        }
        return commLockInfos;
    }

}
