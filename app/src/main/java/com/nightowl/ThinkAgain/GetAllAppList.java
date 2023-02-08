//package com.nightowl.ThinkAgain;
//
//import android.content.pm.PackageInfo;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GetAllAppList {
//
//    class PInfo {
//        private String appname = "";
//        private String pname = "";
//        private String versionName = "";
//        private int versionCode = 0;
//        private Drawable icon;
//        private void prettyPrint() {
//            Log.v(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
//        }
//    }
//
//    private ArrayList<PInfo> getPackages() {
//        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
//        final int max = apps.size();
//        for (int i=0; i<max; i++) {
//            apps.get(i).prettyPrint();
//        }
//        return apps;
//    }
//
//    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
//        ArrayList<PInfo> res = new ArrayList<PInfo>();
//        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
//        for(int i=0;i<packs.size();i++) {
//            PackageInfo p = packs.get(i);
//            if ((!getSysPackages) && (p.versionName == null)) {
//                continue ;
//            }
//            PInfo newInfo = new PInfo();
//            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
//            newInfo.pname = p.packageName;
//            newInfo.versionName = p.versionName;
//            newInfo.versionCode = p.versionCode;
//            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
//            res.add(newInfo);
//        }
//        return res;
//    }
//
//
//}
