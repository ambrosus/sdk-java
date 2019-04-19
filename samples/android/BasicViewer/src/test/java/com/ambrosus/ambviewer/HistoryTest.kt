package com.ambrosus.ambviewer

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.util.Base64
import android.view.Display
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.Identifier
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
class HistoryTest {

    @Test
    fun testGetItems() {
        Base64Mock.mockAndroidBase64Encoding()

        val history = History(TestContext())
        val asset = Asset.Builder().createAsset("0x00001")
        history.addAsset(asset, listOf(Identifier("test", "test")))
        val historyItem = history.getItems()[0]
        Assert.assertEquals(asset, historyItem.asset)
    }

    private class TestSharedPreferences : SharedPreferences {

        val storage = HashMap<String?, String?>()

        override fun contains(key: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getBoolean(key: String?, defValue: Boolean): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getInt(key: String?, defValue: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAll(): Map<String?, *> = storage

        override fun edit(): SharedPreferences.Editor = TestEditor()

        override fun getLong(key: String?, defValue: Long): Long {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getFloat(key: String?, defValue: Float): Float {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getString(key: String?, defValue: String?): String? = storage[key] ?: defValue

        private inner class TestEditor : SharedPreferences.Editor {
            override fun clear(): SharedPreferences.Editor {
                storage.clear()
                return this
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun commit(): Boolean = true

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun apply() {}

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                storage[key] = value
                return this
            }

        }
    }

    private class TestContext : Context() {

        val sharedPreferences = TestSharedPreferences()

        override fun getSharedPreferences(name: String?, mode: Int) = sharedPreferences

        override fun getApplicationContext(): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setWallpaper(bitmap: Bitmap?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setWallpaper(data: InputStream?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeStickyBroadcastAsUser(intent: Intent?, user: UserHandle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkCallingOrSelfPermission(permission: String?): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getClassLoader(): ClassLoader {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkCallingOrSelfUriPermission(uri: Uri?, modeFlags: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getObbDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkUriPermission(uri: Uri?, pid: Int, uid: Int, modeFlags: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkUriPermission(uri: Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getExternalFilesDirs(type: String?): Array<File> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getPackageResourcePath(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteSharedPreferences(name: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkPermission(permission: String?, pid: Int, uid: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startIntentSender(intent: IntentSender?, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startIntentSender(intent: IntentSender?, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendStickyBroadcastAsUser(intent: Intent?, user: UserHandle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getDataDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getWallpaper(): Drawable {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isDeviceProtectedStorage(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getExternalFilesDir(type: String?): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendBroadcastAsUser(intent: Intent?, user: UserHandle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendBroadcastAsUser(intent: Intent?, user: UserHandle?, receiverPermission: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getExternalCacheDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getDatabasePath(name: String?): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getFileStreamPath(name: String?): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun stopService(service: Intent?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkSelfPermission(permission: String?): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, flags: Int): Intent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, broadcastPermission: String?, scheduler: Handler?): Intent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, broadcastPermission: String?, scheduler: Handler?, flags: Int): Intent {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSystemServiceName(serviceClass: Class<*>?): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getMainLooper(): Looper {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceCallingOrSelfPermission(permission: String?, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getPackageCodePath(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkCallingUriPermission(uri: Uri?, modeFlags: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getWallpaperDesiredMinimumWidth(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createDeviceProtectedStorageContext(): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun openFileInput(name: String?): FileInputStream {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getCodeCacheDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteDatabase(name: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getAssets(): AssetManager {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getNoBackupFilesDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActivities(intents: Array<out Intent>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActivities(intents: Array<out Intent>?, options: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getResources(): Resources {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun fileList(): Array<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setTheme(resid: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun unregisterReceiver(receiver: BroadcastReceiver?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforcePermission(permission: String?, pid: Int, uid: Int, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun openFileOutput(name: String?, mode: Int): FileOutputStream {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendStickyOrderedBroadcast(intent: Intent?, resultReceiver: BroadcastReceiver?, scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createConfigurationContext(overrideConfiguration: Configuration?): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getFilesDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendBroadcast(intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendBroadcast(intent: Intent?, receiverPermission: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendOrderedBroadcastAsUser(intent: Intent?, user: UserHandle?, receiverPermission: String?, resultReceiver: BroadcastReceiver?, scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun grantUriPermission(toPackage: String?, uri: Uri?, modeFlags: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceCallingUriPermission(uri: Uri?, modeFlags: Int, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getCacheDir(): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clearWallpaper() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendStickyOrderedBroadcastAsUser(intent: Intent?, user: UserHandle?, resultReceiver: BroadcastReceiver?, scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActivity(intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActivity(intent: Intent?, options: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getPackageManager(): PackageManager {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deleteFile(name: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startService(service: Intent?): ComponentName {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun revokeUriPermission(uri: Uri?, modeFlags: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun revokeUriPermission(toPackage: String?, uri: Uri?, modeFlags: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun moveDatabaseFrom(sourceContext: Context?, name: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startInstrumentation(className: ComponentName?, profileFile: String?, arguments: Bundle?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendOrderedBroadcast(intent: Intent?, receiverPermission: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendOrderedBroadcast(intent: Intent?, receiverPermission: String?, resultReceiver: BroadcastReceiver?, scheduler: Handler?, initialCode: Int, initialData: String?, initialExtras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun unbindService(conn: ServiceConnection?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getApplicationInfo(): ApplicationInfo {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getWallpaperDesiredMinimumHeight(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createDisplayContext(display: Display?): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createContextForSplit(splitName: String?): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTheme(): Resources.Theme {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getPackageName(): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getContentResolver(): ContentResolver {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getObbDirs(): Array<File> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceCallingOrSelfUriPermission(uri: Uri?, modeFlags: Int, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun moveSharedPreferencesFrom(sourceContext: Context?, name: String?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getExternalMediaDirs(): Array<File> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun checkCallingPermission(permission: String?): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getExternalCacheDirs(): Array<File> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun sendStickyBroadcast(intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceCallingPermission(permission: String?, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun peekWallpaper(): Drawable {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSystemService(name: String?): Any {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startForegroundService(service: Intent?): ComponentName {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getDir(name: String?, mode: Int): File {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun databaseList(): Array<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createPackageContext(packageName: String?, flags: Int): Context {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceUriPermission(uri: Uri?, pid: Int, uid: Int, modeFlags: Int, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun enforceUriPermission(uri: Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int, message: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeStickyBroadcast(intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }


    }
}