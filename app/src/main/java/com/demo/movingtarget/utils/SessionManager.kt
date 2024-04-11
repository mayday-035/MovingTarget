package com.demo.movingtarget.utils

import android.content.Context
import android.content.SharedPreferences


class SessionManager(mcxt: Context) {

    companion object {
        val PREF_GENERAL = "PREF_GENERAL"

        const val KEY_START_TIMER_SOUND = "START_TIMER_SOUND"
        const val KEY_END_TIMER_SOUND = "END_TIMER_SOUND"

    }


    var generalEditor: SharedPreferences.Editor
    var generalPref: SharedPreferences

    private var PRIVATE_MODE = 0

    init {
        generalPref = mcxt.getSharedPreferences(PREF_GENERAL, PRIVATE_MODE)
        generalEditor = generalPref.edit()
    }


    var startTimerSound: Boolean
        get() = generalPref.getBoolean(KEY_START_TIMER_SOUND, true)
        set(status) {
            generalEditor.putBoolean(KEY_START_TIMER_SOUND, status)
            generalEditor.commit()
        }

    var endTimerSound: Boolean
        get() = generalPref.getBoolean(KEY_END_TIMER_SOUND, true)
        set(status) {
            generalEditor.putBoolean(KEY_END_TIMER_SOUND, status)
            generalEditor.commit()
        }
}