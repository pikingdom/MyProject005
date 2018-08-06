#-optimizationpasses 5
-ignorewarnings
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*



-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep public class com.isw.**{
	*;
}

-keep public class android.support.v4.**{
	*;
}

-keep public class com.nd.analytics.**{
	*;
}

-keep public class android.app.PluginActivityGroup
-keepclassmembers class android.app.PluginActivityGroup {
   *;
}

-keep public class android.app.PluginListActivity
-keepclassmembers class android.app.PluginListActivity {
   *;
}

-keep public class android.app.PluginTabActivity
-keepclassmembers class android.app.PluginTabActivity {
   *;
}


-keep public class android.database.sqlite.PluginDBHelper
-keepclassmembers class android.database.sqlite.PluginDBHelper {
   *;
}
-keep public class com.android.dynamic.**
-keepclassmembers class com.android.dynamic.** {
   *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn android.app.PluginActivityGroup
-dontwarn android.app.PluginListActivity
-dontwarn android.app.PluginTabActivity
-dontwarn android.database.sqlite.PluginDBHelper
-dontwarn com.android.dynamic.**
-dontwarn com.nd.hilauncherdev.plugin.navigation.pluginAD.PluginContextBase
-dontwarn com.nd.hilauncherdev.plugin.navigation.pluginAD.PluginContextBase$1
-dontwarn com.nd.hilauncherdev.plugin.navigation.widget.NavigationView
-dontwarn com.nd.hilauncherdev.plugin.navigation.widget.SohuPageView$1
-dontwarn android.support.v4.**
-keep class com.nd.hilauncherdev.plugin.navigation.widget.NavigationView {*;}
-keep class com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationView {*;}
-keep class com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController {*;}
-keep class com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis {*;}
-dontwarn com.nd.hilauncherdev.plugin.navigation.widget.SohuPageView
-keep class com.sohu.newsclient.lite.** {public *;}
-keep class com.sohu.android.plugin.** {*;}
-keep class com.sohu.news.** {*;}
-keep class android.support.v4.** {!private *;}
-keep class com.sohu.newssdk.** {*;}
-keep class com.nd.hilauncherdev.plugin.navigation.util.NavigationHelper {*;}
-dontwarn com.nd.hilauncherdev.launcher.**
-keep class com.nd.hilauncherdev.plugin.navigation.BuildConfig {*;}
-keep class * extends com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoader {*;}
-keep class * extends com.nd.hilauncherdev.plugin.navigation.widget.CardInnerViewHelperBase {*;}
