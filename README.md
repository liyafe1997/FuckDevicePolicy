[![Download](https://img.shields.io/github/downloads/liyafe1997/AllowUnknowSourceWorkProfile/total)](https://github.com/liyafe1997/AllowUnknowSourceWorkProfile/releases)

# English

[简体中文](#%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87-simplified-chinese)

This module can let you make some user restriction policies (set by Device Admin App or Work Profile, e.g. Microsoft Intune) nonfunctional. Especially some device-wide policies, even it is in the Work Profile but affected the outside  whole android environment(Your Personal Profile).

For example, since Android 10, a new feature for Work Profile added: [https://developer.android.com/work/versions/android-10#work_profile_device-wide_unknown_sources](https://developer.android.com/work/versions/android-10#work_profile_device-wide_unknown_sources)

This allow the Work Profile App (For example, Microsoft Intune, aka Company Portal) disable the permission of installing APKs from unknown source GLOBALLY, even its inside the Work Profile, then you also can not install APKs in your personal profile! 

Another example of device-wide restriction is ```ensure_verify_apps```, it can make Google Play Protect always enable.

If you are facing these problem, just use this Xposed module. 

Check [https://developer.android.com/reference/android/os/UserManager](https://developer.android.com/reference/android/os/UserManager) to get all restriction policy values (See [Constant Value]). Put those you want to disable in this module's App, one line one policy value.

Use `dumpsys device_policy` (run via `adb shell` or root) to check what restriction policies applied on your device (under [userRestrictions:] section).

Only apply this module to [System Framework] (which is the default scope setting), no need to apply to any other apps, especially DO NOT apply to Microsoft Intune, otherwise your Xposed and ROOT will be detected.

Due to LSPosed's [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences), if you are using **LSPosed**, please **Activate this module and reboot firstly, before edit and save your custom policies!!!** (In another word, when you click the save button, this module should be activated, if you are using LSPosed). Otherwise your custom policies will not be saved correctly for LSPosed.

# 简体中文 (Simplified Chinese)

该模块可以让一些Android的用户限制策略失效，这些策略一般由“设备管理员”App设置，或者“工作配置文件”。比如Microsoft Intune（又叫“公司门户”）就是用来干这事的。特别是一些全局策略，即使是在工作配置文件里面设置的，但是会影响到外面的整个安卓（你的个人空间，即安卓的主环境）。

比如，从Android 10开始，“工作配置文件”加了个很扯的功能：“device-wide unknown sources”。让你全局（哪怕在工作配置外面，即你的个人环境）都无法安装APK。（见[https://developer.android.com/work/versions/android-10#work_profile_device-wide_unknown_sources](https://developer.android.com/work/versions/android-10#work_profile_device-wide_unknown_sources)）

有的企业管理App，比如微软的Microsoft Intune（也叫公司门户）可能会应用这个策略（根据你的公司设置）。

另一个例子是 ```ensure_verify_apps```，这会强行让Google Play保护机制打开，无法关闭（即使在你的个人空间个人帐号的Google Play商店）。

如果你遇到这些问题，可以使用这个Xposed模块。

可以在[https://developer.android.com/reference/android/os/UserManager](https://developer.android.com/reference/android/os/UserManager)找到所有的用户限制策略的值（见Constant Value），把你想禁用的写到这个模块的App里，一行一个。

如果你想看有哪些策略应用在了你的设备上，可以运行`dumpsys device_policy`（用adb shell来运行，或者通过root权限在一些终端如Termux上跑），见[userRestrictions:]字段。

这个模块的作用域只需要勾选“系统框架”（即保持默认设置即可），不需要勾选其它App，特别不要勾上Microsoft Intune，不然Xposed和ROOT会被它检测到。

由于LSPosed的[New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences)机制，如果你用的是**LSPosed**，在App中编辑/保存策略你的自定义策略之前，**请先激活这个模块并重启！！！**（换句话说，**当你点击保存按钮时，这个模块应处于激活状态！**），否则你保存的策略无法在LSPosed环境中正确读取。
