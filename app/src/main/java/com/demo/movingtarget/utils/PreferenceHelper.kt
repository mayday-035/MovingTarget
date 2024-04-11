package com.demo.movingtarget.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.method.TextKeyListener.clear
import java.io.File

object PreferenceHelper {

    /**
     * namaz prayer settngs are stored in custom preference name @param[PRAYER_SETTINGS_PREFERENCE]
     */
    val PRAYER_SETTINGS_PREFERENCE = "prayer_setting_preference"

    fun defaultPrefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)


    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    fun SharedPreferences.delete(){
        val editor: SharedPreferences.Editor = this.edit()
        editor.clear()
        editor.commit()
    }

     fun clearAllSharedPreferences(context: Context) {
        val sharedPreferencesPath = File(context.filesDir.parentFile!!.absolutePath + File.separator + "shared_prefs")
        sharedPreferencesPath.listFiles()?.forEach { file ->
            context.getSharedPreferences(file.nameWithoutExtension, Context.MODE_PRIVATE).edit { editor -> editor.clear() }
        }
    }

    operator fun SharedPreferences.set(key: String, value: Any?) = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T

        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}