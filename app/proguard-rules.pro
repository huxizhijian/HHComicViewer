# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\wei\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#databinding混淆设置
-keep class android.databinding.** { *; }
-dontwarn sun.misc.**
-dontwarn android.databinding.**

# xUtils混淆设置
-keepattributes Signature,*Annotation*
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}

#okhttp混淆设置
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }

#okio
-dontwarn okio.**
-keep class okio.**{*;}
-keep interface okio.**{*;}

#glide配置文件混淆设置
-keep public class * implements com.bumptech.glide.module.GlideModule

#MobShareSDK混淆设置
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

#新增设置
-dontwarn org.apache.velocity.**
-keep class org.apache.velocity.**{*;}
-keep interface org.apache.velocity.**{*;}

-dontwarn com.google.auto.**
-keep class com.google.auto.**{*;}
-keep interface com.google.auto.**{*;}

-dontwarn autovalue.shaded.**
-keep class autovalue.shaded.**{*;}
-keep interface autovalue.shaded.**{*;}

-dontwarn net.rdrei.android.dirchooser.**
-keep class net.rdrei.android.dirchooser.**{*;}
-keep interface net.rdrei.android.dirchooser.**{*;}

#jsoup不混淆
-dontwarn org.jsoup.**
-keep class org.jsoup.**{*;}
-keep interface org.jsoup.**{*;}

#自己定义的model不混淆
-dontwarn org.huxizhijian.sdk.**
-keep class org.huxizhijian.sdk.**{*;}
-keep interface org.huxizhijian.sdk.**{*;}

#实体类不要混淆
-dontwarn org.huxizhijian.hhcomicviewer2.model.**
-dontwarn org.huxizhijian.hhcomicviewer2.adapter.entity.**
-keep class org.huxizhijian.hhcomicviewer2.model.**{*;}
-keep class org.huxizhijian.hhcomicviewer2.adapter.entity.**{*;}
-keep interface org.huxizhijian.hhcomicviewer2.model.**{*;}
-keep interface org.huxizhijian.hhcomicviewer2.adapter.entity.**{*;}

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

#缺省proguard 会检查每一个引用是否正确，但是第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，系统就会报错。
-dontwarn android.support.v4.**
-dontwarn android.os.**
-keep class android.support.v4.** { *; }        # 保持哪些类不被混淆
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class android.os.**{*;}

-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.widget
-keep public class * extends com.sqlcrypt.database
-keep public class * extends com.sqlcrypt.database.sqlite

-keepclasseswithmembernames class * {     # 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {         # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {         # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { #保持类成员
   public void *(android.view.View);
}

-keepclassmembers enum * {                  # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {    # 保持Parcelable不被混淆
  public static final android.os.Parcelable$Creator *;
}

-optimizationpasses 5                   # 指定代码的压缩级别
-dontusemixedcaseclassnames             # 是否使用大小写混合
-dontpreverify                          # 混淆时是否做预校验
-verbose                                # 混淆时是否记录日志
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclassmembers   # 指定不去忽略非公共的库的类的成员
