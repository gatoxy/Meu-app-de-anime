## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.kts.kts.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
#
#-keep class * extends androidx.room.RoomDatabase
#-keep @androidx.room.Entity class *
#-dontwarn androidx.room.paging.**
#
## Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
## EnclosingMethod is required to use InnerClasses.
#-keepattributes Signature, InnerClasses, EnclosingMethod
#
## Retrofit does reflection on method and parameter annotations.
#-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
#
## Keep annotation default values (e.g., retrofit2.http.Field.encoded).
#-keepattributes AnnotationDefault
#
## Retain service method parameters when optimizing.
#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
#
## Ignore annotation used for build tooling.
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
## Ignore JSR 305 annotations for embedding nullability information.
#-dontwarn javax.annotation.**
#
## Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
#-dontwarn kotlin.Unit
#
## Top-level functions that can only be used by Kotlin.
#-dontwarn retrofit2.KotlinExtensions
#-dontwarn retrofit2.KotlinExtensions*
#
## With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
## and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
#-if interface * { @retrofit2.http.* <methods>; }
#-keep,allowobfuscation interface <1>
#
## Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
#-keep,allowobfuscation,allowshrinking interface retrofit2.Call
#-keep,allowobfuscation,allowshrinking class retrofit2.Response
#
## With R8 full mode generic signatures are stripped for classes that are not
## kept. Suspend functions are wrapped in continuations where the type argument
## is used.
#-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
#
## Accessed via menu.xml
-keep class androidx.mediarouter.app.MediaRouteActionProvider {
  *;
}
#
-keep class * extends com.airbnb.epoxy.EpoxyController { *; }
-keep class * extends com.airbnb.epoxy.ControllerHelper { *; }
-keepclasseswithmembernames class * { @com.airbnb.epoxy.AutoModel <fields>; }
-keep class com.kl3jvi.animity.data.model.ui_models.AniListMedia
-keep class com.kl3jvi.animity.data.model.ui_models.Review
-keep public class com.kl3jvi.animity.data.model.auth_models.AuthResponse {*;}
-keep public class com.kl3jvi.animity.data.model.ui_models.DetailedAnimeInfo
-keep class com.kl3jvi.animity.data.model.ui_models** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.**{*;}

-dontwarn com.oracle.svm.core.annotate.**
-dontwarn okhttp3.internal.graal.**
-dontwarn org.graalvm.nativeimage.**