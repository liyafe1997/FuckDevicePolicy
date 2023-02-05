package com.strawing.fuckdevicepolicy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainModule implements IXposedHookLoadPackage {

    public static final String packageName = "com.strawing.fuckdevicepolicy";
    public static final String prefsName = "policies";
    public static final String prefsDirPath = "/data/user_de/0/" + packageName + "/shared_prefs";
    public static final String prefsFilePath = prefsDirPath + "/" + prefsName + ".xml";

    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android") && lpparam.processName.equals("android")) {
            // Load policy values
            XposedBridge.log("[FuckDevicePolicy] Loading policy values......");
            XSharedPreferences pref = null;
            try {
                if (XposedBridge.getXposedVersion() >= 93)
                    pref = new XSharedPreferences(packageName, prefsName);
                else
                    pref = new XSharedPreferences(new File(prefsFilePath));
                pref.makeWorldReadable();
            } catch (Throwable t) {
                XposedBridge.log("[FuckDevicePolicy] Error on loading policy values: " + t);
            }

            String policy_str = pref.getString("policies_string", "no_install_unknown_sources_globally\nno_debugging_features");
            if (policy_str.equals("")) {
                XposedBridge.log("[FuckDevicePolicy] No policies set, module will exits and hooks will not be load.");
                return;
            }
            final String[] policiesStrArray = policy_str.split("\n");
            ArrayList<String> policyList = new ArrayList();

            XposedBridge.log("[FuckDevicePolicy] Fuck these policies:");
            for (String policy : policiesStrArray) {
                String policy_trimmed = policy.trim();
                if (policy_trimmed.equals(""))
                    continue;
                XposedBridge.log("[FuckDevicePolicy] [" + policy_trimmed + "]");
                policyList.add(policy_trimmed);
            }
            XposedBridge.log("[FuckDevicePolicy] Policies loaded.");
            XC_MethodHook returnFalseHook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                    try {
                        String key = (String) param.args[0];
                        if (policyList.contains(key)) {
                            param.setResult(false);
                        }
                    } catch (Exception e) {
                        XposedBridge.log("[FuckDevicePolicy] Error on hooked method: " + e.toString());
                    }
                }

            };
            XposedBridge.log("[FuckDevicePolicy] Loading hook......");
            try {
                XposedHelpers.findAndHookMethod("com.android.server.pm.UserManagerService", lpparam.classLoader, "getUserRestrictionSource", String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                        try {
                            String key = (String) param.args[0];
                            if (policyList.contains(key)) {
                                param.setResult(0);
                            }
                        } catch (Exception e) {
                            XposedBridge.log("[FuckDevicePolicy] Error in hooked getUserRestrictionSource: " + e.toString());
                        }
                    }

                });
            } catch (Exception e) {

                XposedBridge.log("[FuckDevicePolicy] Error on apply hook[getUserRestrictionSource]: " + e.toString());
            }


            try {
                XposedHelpers.findAndHookMethod("com.android.server.pm.UserManagerService", lpparam.classLoader, "getUserRestrictionSources", String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                        try {
                            String key = (String) param.args[0];
                            if (policyList.contains(key)) {
                                param.setResult(Collections.emptyList());
                            }
                        } catch (Exception e) {
                            XposedBridge.log("[FuckDevicePolicy] Error in hooked getUserRestrictionSources: " + e.toString());
                        }
                    }

                });
            } catch (Exception e) {

                XposedBridge.log("[FuckDevicePolicy] Error on apply hook[getUserRestrictionSources]: " + e.toString());
            }
            try {
                XposedHelpers.findAndHookMethod("com.android.server.pm.UserManagerService", lpparam.classLoader, "hasUserRestriction", String.class, int.class, returnFalseHook);

            } catch (Exception e) {

                XposedBridge.log("[FuckDevicePolicy] Error on apply hook[hasUserRestriction]: " + e.toString());
            }
            try {
                XposedHelpers.findAndHookMethod("com.android.server.pm.UserManagerService", lpparam.classLoader, "hasUserRestrictionOnAnyUser", String.class, returnFalseHook);
            } catch (Exception e) {

                XposedBridge.log("[FuckDevicePolicy] Error on apply hook[hasUserRestrictionOnAnyUser]: " + e.toString());
            }
            XposedBridge.log("[FuckDevicePolicy] Hooks applied.");
        }
    }
}
