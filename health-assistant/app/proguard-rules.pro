# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep TensorFlow Lite classes
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.support.** { *; }

# Keep Health Connect classes
-keep class androidx.health.connect.** { *; }

# Keep Jitsi Meet classes
-keep class org.jitsi.meet.sdk.** { *; }
-keep class org.webrtc.** { *; }

# Keep Retrofit classes
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

# Keep iText PDF classes
-keep class com.itextpdf.** { *; }

# Keep OpenCSV classes
-keep class com.opencsv.** { *; }

# Keep MPAndroidChart classes
-keep class com.github.mikephil.charting.** { *; }

# Keep data classes
-keep class com.healthassistant.data.** { *; }
-keep class com.healthassistant.health.** { *; }
-keep class com.healthassistant.ml.** { *; }
-keep class com.healthassistant.telehealth.** { *; }
-keep class com.healthassistant.export.** { *; }

# Keep model classes
-keep class * extends java.io.Serializable { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
