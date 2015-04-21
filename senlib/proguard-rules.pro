# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:/eclipse4.3_and_androidSDK/sdk/tools/proguard/proguard-android.txt
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

#ָ�������ѹ������
-optimizationpasses 5
#��������ϴ�Сд
-dontusemixedcaseclassnames
#��ȥ���Էǹ����Ŀ���
-dontskipnonpubliclibraryclasses
 #�Ż�  ���Ż���������ļ�
-dontoptimize
 #ԤУ��
-dontpreverify
 #����ʱ�Ƿ��¼��־
-verbose
 # ����ʱ�����õ��㷨
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#����ע��
-keepattributes *Annotation*
# ������Щ�಻������
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep public class com.sen.lib.view.HorizontalItemTab
-keep public class com.sen.lib.view.ItemTabAdpater
-keep public class com.sen.lib.support.CustomViewPager
#���������v4�����������������
-keep public class android.support.v4.view.ViewPager.OnPageChangeListener
#���Ծ���
-ignorewarning
#####################��¼���ɵ���־����,gradle buildʱ�ڱ���Ŀ��Ŀ¼���################
#apk �������� class ���ڲ��ṹ
#-dump class_files.txt
#δ��������ͳ�Ա
#-printseeds seeds.txt
#�г��� apk ��ɾ���Ĵ���
#-printusage unused.txt
#����ǰ���ӳ��
#-printmapping mapping.txt
