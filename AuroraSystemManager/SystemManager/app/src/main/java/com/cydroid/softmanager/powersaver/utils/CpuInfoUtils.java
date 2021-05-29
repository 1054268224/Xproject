package com.cydroid.softmanager.powersaver.utils;

import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;

import com.cydroid.softmanager.utils.Log;
import com.mediatek.perfservice.IPerfService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CpuInfoUtils {
    private static final String TAG = "CpuInfoUtils";

    private static final String MTK_PERF_SERVICE = "mtk-perfservice";
    private static IPerfService mPerfService = null;
    private static final String OBJ_PTAH = "data/data/com.cydroid.softmanager/cpuinfo.obj";
    private static final int CMD_SET_CPU_CORE_MIN = 0;
    private static final int CMD_SET_CPU_CORE_MAX = 1;
    private static final int CMD_SET_CPU_FREQ_MAX = 5;
    private static final int CMD_SET_SCREEN_OFF_STATE = 12;
    private static final int DEFAULT_LIMIT_CPU_CORE = 4;
    // Gionee <yangxinruo> <2016-1-26> add for CR01621516 begin
    private static final int DEFAULT_LIMIT_CPU_FREQ = 1200000;
    // Gionee <yangxinruo> <2016-1-26> add for CR01621516 end
    private static int mPerfServiceinited = 0;
    private static int mPerfServiceHandle = -1;
    private static Map<String, Integer> mHashMap = new HashMap<String, Integer>();

    static final HashMap<String, Integer> mCpuCore = new HashMap<String, Integer>();
    static final HashMap<String, Integer> mCpuFreq = new HashMap<String, Integer>();
    private static final String mModel = SystemProperties.get("ro.product.model", "android");

    static {
        mCpuCore.put("GN9006", 4);
        mCpuFreq.put("GN9006", 1200);

        mCpuCore.put("GN9011", 4);
        mCpuFreq.put("GN9011", 1200000);// 6755 新API单位是kHz
    }

    public static void limitMutliCore(boolean enable) {
        if (getCpuCore() < 0) {
            Log.d(TAG, "model " + mModel + " not support");
            return;
        }
        perfServiceInit();
        if (mPerfService != null && mPerfServiceHandle != -1) {
            try {
                Log.d(TAG, "enableMutliCore enable--> " + enable + ", mPerfServiceHandle -->"
                        + mPerfServiceHandle);
                if (enable) {
                    mPerfService.userEnable(mPerfServiceHandle);
                    mHashMap.put("mPerfServiceHandle", mPerfServiceHandle);
                    mHashMap.put("mPerfServiceinited", mPerfServiceinited);
                    saveObject();
                } else {
                    mPerfService.userDisable(mPerfServiceHandle);
                    mHashMap.clear();
                    saveObject();
                }
            } catch (RemoteException e) {
                Log.d(TAG, "ERR: RemoteException in mutliCoreEnable:" + e);
            }
        }
    }

    private static void perfServiceInit() {
        getObject();
        if (mHashMap != null) {
            mPerfServiceinited = mHashMap.get("mPerfServiceinited") != null
                    ? mHashMap.get("mPerfServiceinited") : 0;
            mPerfServiceHandle = mHashMap.get("mPerfServiceHandle") != null
                    ? mHashMap.get("mPerfServiceHandle") : -1;
        }

        IBinder b = ServiceManager.checkService(MTK_PERF_SERVICE);
        if (b != null) {
            mPerfService = IPerfService.Stub.asInterface(b);
            if (mPerfServiceinited == 0) {
                if (mPerfService != null) {
                    try {
                        mPerfServiceHandle = mPerfService.userReg(1, 0, Process.myPid(), Process.myTid());
                        Log.d(TAG, "userReg mPerfServiceHandle " + mPerfServiceHandle);
                        // 1 --> 暗屏仍然生效 2--->暗频失效,亮频生效
                        mPerfService.userRegScnConfig(mPerfServiceHandle, CMD_SET_SCREEN_OFF_STATE, 2, 0, 0,
                                0);
                        // 设置cpu核数
                        mPerfService.userRegScnConfig(mPerfServiceHandle, CMD_SET_CPU_CORE_MAX, getCpuCore(),
                                -1, -1, -1);
                        // 设置cpu频率
                        mPerfService.userRegScnConfig(mPerfServiceHandle, CMD_SET_CPU_FREQ_MAX, getCpuFreq(),
                                -1, -1, -1);

                        mPerfServiceinited = 1;
                    } catch (RemoteException e) {
                        Log.e(TAG, "ERR: RemoteException in perfServiceInit:" + e);
                    } catch (Exception ex) {
                        Log.e(TAG, "ERR: Exception in perfServiceInit:" + ex);
                    }
                }
            }
        }
    }

    private static void saveObject() {
        try {
            File objFile = new File(OBJ_PTAH);
            if (objFile.exists() && objFile.delete()) {
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(OBJ_PTAH));
            out.writeObject(mHashMap);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(TAG, "saveObject-------->", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void getObject() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(OBJ_PTAH));
            mHashMap = (HashMap<String, Integer>) in.readObject();
            in.close();
        } catch (Exception e) {
            Log.e(TAG, "getObject-------->", e);
        }
    }

    public static void deleteCpuInfoObj() {
        try {
            File objFile = new File(OBJ_PTAH);
            if (objFile.exists() && objFile.delete()) {
                Log.e(TAG, "deleteObject ok-------->");
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteObject-------->", e);
        }
    }

    private static int getCpuCore() {
        if (mCpuCore.get(mModel) != null) {
            return mCpuCore.get(mModel);
        } else {
            return DEFAULT_LIMIT_CPU_CORE;
        }
    }

    private static int getCpuFreq() {
        if (mCpuFreq.get(mModel) != null) {
            return mCpuFreq.get(mModel);
        } else {
            return DEFAULT_LIMIT_CPU_FREQ;
        }
    }
}