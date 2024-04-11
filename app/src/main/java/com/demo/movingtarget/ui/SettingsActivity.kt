package com.demo.movingtarget.ui

import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.ActivitySettingsBinding
import com.demo.movingtarget.utils.SessionManager

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        var tCnt = 2//sharedPref.getString("count",null)
        var tPtrn = 0//sharedPref.getString("pattern",null)
        var tSel= 0//sharedPref.getString("selection",null)
        var tTwoSel = 0//sharedPref.getString("selectionTwo",null)
        var sAwake = false//sharedPref.getString("awake",false)
        var dMode = false//sharedPref.getBoolean("dark",false)
        var tDisplay = false//
    }
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)
        binding.startTimerCheckBox.isChecked = session.startTimerSound
        binding.endTimerCheckBox.isChecked = session.endTimerSound
        var stayAwakeCheckBox = findViewById<CheckBox>(R.id.stayAwakeCheckBox)
        var darkModeCheckBox = findViewById<CheckBox>(R.id.darkModeCheckBox)
        var displayTimerCheckBox = findViewById<CheckBox>(R.id.displayTimerCheckBox)
        setOnClickListeners()
        val toneTwo = findViewById<TextView>(R.id.toneTwoSelection)
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        binding.apply{
            saveProfile.setOnClickListener{
                val tCount = toneCountSpinner.selectedItem.toString()
                val tPattern = tonePatternSpinner.selectedItem.toString()
                val tSelection = toneSelectionSpinner.selectedItem.toString()
                val tTwoSelection = toneTwoSelectionSpinner.selectedItem.toString()
                val stayAwake = stayAwakeCheckBox.isChecked
                val darkMode = darkModeCheckBox.isChecked
                val timerDisplay = displayTimerCheckBox.isChecked
                editor.apply {
                    putString("count",tCount)
                    putString("pattern",tPattern)
                    putString("selection",tSelection)
                    putString("selectionTwo",tTwoSelection)
                    putBoolean("awake",stayAwake)
                    putBoolean("dark",darkMode)
                    putBoolean("timerD",timerDisplay)
                    apply()
                }
                if (stayAwake != null){
                    if (stayAwake == true){
                        stayAwakeCheckBox.isChecked = true
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }else {stayAwakeCheckBox.isChecked = false
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                    }
                if (darkMode != null){
                    if (darkMode == true){
                        darkModeCheckBox.isChecked = true
                    }else {darkModeCheckBox.isChecked = false}
                }
                if (timerDisplay != null){
                    if (timerDisplay == true){
                        displayTimerCheckBox.isChecked = true
                    }else {displayTimerCheckBox.isChecked = false}
                }
                if (darkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                updatePreferences(tCount, tPattern, tSelection, tTwoSelection, timerDisplay)

            }
            clearProfile.setOnClickListener {
                //need to set field values and
                val tCount = ""
                val tPattern = ""
                val tSelection = ""
                val tTwoSelection = ""
                val stayAwake = false
                val darkMode = false
                val timerDisplay = false
                stayAwakeCheckBox.isChecked = false
                darkModeCheckBox.isChecked = false
                displayTimerCheckBox.isChecked = false
                toneCountSpinner.setSelection(0)
                tonePatternSpinner.setSelection(0)
                toneSelectionSpinner.setSelection(0)
                toneTwoSelectionSpinner.setSelection(0)

                editor.apply {
                    putString("count",tCount)
                    putString("pattern",tPattern)
                    putString("selection",tSelection)
                    putString("selectionTwo",tTwoSelection)
                    putBoolean("awake",stayAwake)
                    putBoolean("dark",darkMode)
                    putBoolean("timerD",timerDisplay)
                    apply()
                }
            }

        }

        val patterns = arrayOf<String?>("Select pattern","1111","1010","1001","10001")
        val spinnerTonePattern: Spinner = findViewById<Spinner>(R.id.tonePatternSpinner);
        val adapterTonePattern: ArrayAdapter<Any?> = ArrayAdapter<Any?>(this, R.layout.spinner_list, patterns);
        adapterTonePattern.setDropDownViewResource(R.layout.spinner_list);
        spinnerTonePattern.adapter = adapterTonePattern
        //spinnerTonePattern.prompt="select prefered tone"

        val count = arrayOf<String?>("Select Count","Non-Stop","Two","Three","Four","Six","Eight")
        val spinnerToneCount: Spinner = findViewById<Spinner>(R.id.toneCountSpinner);
        val adapterToneCount: ArrayAdapter<Any?> = ArrayAdapter<Any?>(this, R.layout.spinner_list, count);
        adapterToneCount.setDropDownViewResource(R.layout.spinner_list);
        spinnerToneCount.adapter = adapterToneCount

        val tone = arrayOf<String?>("Select Tone","bubble_click", "short_click", "loud_click", "long_beep")
        val spinnerToneSelection: Spinner = findViewById<Spinner>(R.id.toneSelectionSpinner);
        val adapterToneSelection: ArrayAdapter<Any?> = ArrayAdapter<Any?>(this, R.layout.spinner_list, tone);
        adapterToneSelection.setDropDownViewResource(R.layout.spinner_list);
        spinnerToneSelection.adapter = adapterToneSelection

        val tTone = arrayOf<String?>("Select Tone","bubble_click", "short_click", "loud_click", "long_beep")
        val spinnerToneTwoSelection: Spinner = findViewById<Spinner>(R.id.toneTwoSelectionSpinner);
        val adapterToneTwoSelection: ArrayAdapter<Any?> = ArrayAdapter<Any?>(this, R.layout.spinner_list, tTone);
        adapterToneTwoSelection.setDropDownViewResource(R.layout.spinner_list);
        spinnerToneTwoSelection.adapter = adapterToneTwoSelection

        val tCount = sharedPref.getString("count",null)
        val tCountPosition: Int = adapterToneCount.getPosition(tCount)
        spinnerToneCount.setSelection(tCountPosition)

        val tPattern = sharedPref.getString("pattern",null)
        val tPatternPosition: Int = adapterTonePattern.getPosition(tPattern)
        spinnerTonePattern.setSelection(tPatternPosition)

        val tSelection = sharedPref.getString("selection",null)
        val tSelectionPosition: Int = adapterToneSelection.getPosition(tSelection)
        spinnerToneSelection.setSelection(tSelectionPosition)

        val tTwoSelection = sharedPref.getString("selectionTwo",null)
        val tTwoSelectionPosition: Int = adapterToneSelection.getPosition(tTwoSelection)
        spinnerToneTwoSelection.setSelection(tTwoSelectionPosition)
        //if (tTwoSelection != null){
          //  Toast.makeText(this, tTwoSelection, Toast.LENGTH_LONG).show()
        //}
        val stayAwake = sharedPref.getBoolean("awake",false)
        val darkMode = sharedPref.getBoolean("dark",false)
        val timerDisplay = sharedPref.getBoolean("timerD",false)

        if (stayAwake != null){
            if (stayAwake == true){
                stayAwakeCheckBox.isChecked = true
            }else {stayAwakeCheckBox.isChecked = false
                if (::wakeLock.isInitialized && wakeLock.isHeld) {
                    releaseWakeLock()
                }
            }
        }
        if (darkMode != null){
            if (darkMode == true){
                darkModeCheckBox.isChecked = true
            }else {darkModeCheckBox.isChecked = false}
        }
        if (timerDisplay != null){
            if (timerDisplay == true){
                displayTimerCheckBox.isChecked = true
            }else {displayTimerCheckBox.isChecked = false}
        }
        updatePreferences(tCount, tPattern, tSelection, tTwoSelection, timerDisplay)


    }
    // Function to acquire a wakelock
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MovingTarget:WakeLockTag")
        wakeLock.acquire()
    }

    // Function to release the wakelock
    private fun releaseWakeLock() {
        // Release the wakelock
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun setOnClickListeners() {
        binding.backArrow.setOnClickListener(this@SettingsActivity)
        binding.startTimerCheckBox.setOnClickListener(this@SettingsActivity)
        binding.endTimerCheckBox.setOnClickListener(this@SettingsActivity)
    }

    fun updatePreferences(tCount: String?, tPattern: String?, tSelection: String?, tTwoSelection: String?, timerDisplay: Boolean?) {
        if (tCount != null) {
            when {
                tCount.startsWith("N") -> {
                    MainActivity.toneCount = 99999
                    tCnt = 99999
                }
                tCount.startsWith("Tw") -> {
                    MainActivity.toneCount = 2
                    tCnt = 2
                }
                tCount.isBlank() -> {
                    MainActivity.toneCount = 2
                    tCnt = 2
                }
                tCount.startsWith("Th") -> {
                    MainActivity.toneCount = 3
                    tCnt = 3
                }
                tCount.startsWith("F") -> {
                    MainActivity.toneCount = 4
                    tCnt = 4
                }
                tCount.startsWith("Si") -> {
                    MainActivity.toneCount = 6
                    tCnt = 6
                }
                tCount.startsWith("E") -> {
                    MainActivity.toneCount = 8
                    tCnt = 8
                }
                tCount.startsWith("Se") -> {
                    MainActivity.toneCount = 0
                    tCnt = 0
                }
            }
        }

        // Similar logic for other variables
        if (tPattern != null) {
            when {
                tPattern.startsWith("C") -> {
                    MainActivity.tonePattern = 1
                    tPtrn = 1
                }
                tPattern.startsWith("A") -> {
                    MainActivity.tonePattern = 2
                    tPtrn = 2
                }
            }
        }

        if (tSelection != null) {
            when {
                tSelection.startsWith("b") -> {
                    MainActivity.toneSound = 0
                    tSel = 0
                }
                tSelection.startsWith("s") -> {
                    MainActivity.toneSound = 1
                    tSel = 1
                }
                tSelection.startsWith("lou") -> {
                    MainActivity.toneSound = 2
                    tSel = 2
                }
                tSelection.startsWith("lon") -> {
                    MainActivity.toneSound = 3
                    tSel = 3
                }
            }
        }

        if (tTwoSelection != null) {
            when {
                tTwoSelection.startsWith("b") -> {
                    MainActivity.toneTwoSound = 0
                    tTwoSel = 0
                }
                tTwoSelection.startsWith("s") -> {
                    MainActivity.toneTwoSound = 1
                    tTwoSel = 1
                }
                tTwoSelection.startsWith("lou") -> {
                    MainActivity.toneTwoSound = 2
                    tTwoSel = 2
                }
                tTwoSelection.startsWith("lon") -> {
                    MainActivity.toneTwoSound = 3
                    tTwoSel = 3
                }
            }
        }
        if (timerDisplay != null) {
            when {
                timerDisplay == true -> {
                    MainActivity.timeDisplay = true
                    tDisplay = true
                }
                timerDisplay == false -> {
                    MainActivity.timeDisplay = false
                    tDisplay = false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backArrow -> finish()
            R.id.startTimerCheckBox -> session.startTimerSound = !session.startTimerSound
            R.id.endTimerCheckBox -> session.endTimerSound = !session.endTimerSound
        }
    }
}