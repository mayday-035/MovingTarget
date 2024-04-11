package com.demo.movingtarget.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Display
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.demo.movingtarget.R
import com.demo.movingtarget.ui.M1Fragment
import com.demo.movingtarget.ui.M2Fragment
import com.demo.movingtarget.ui.M3Fragment
import com.demo.movingtarget.ui.M4Fragment
import com.demo.movingtarget.databinding.ActivityMainBinding
import com.demo.movingtarget.utils.PreferenceHelper
import java.text.MessageFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var wakeLock: PowerManager.WakeLock
    private var originalBrightness: Float = 0f
    private var bright = 0
    private var darkOverlay: View? = null
    private var initialWidth = 0
    private var initialHeight = 0
    var currentFragment: Fragment? = null
    //var currentFragment = ""
    var millisecondTime: Long = 0
    var startTime:kotlin.Long = 0
    var timeBuff:kotlin.Long = 0
    var updateTime:kotlin.Long = 0L
    var newTime:kotlin.Long = 0
    var seconds:Long = 0
    var minutes:Long = 0
    var milliSeconds:Long = 0
    val handler = Handler()
    var table = "m1"
    companion object {
        val audio : Array<Int> = arrayOf(R.raw.bubble_click, R.raw.short_click, R.raw.loud_click, R.raw.long_beep)
        var toneCount: Int = SettingsActivity.tCnt
        var tonePattern: Int = 0
        var toneSound: Int = 0
        var toneTwoSound: Int = 0
        var continueLoop = true
        var timeDisplay = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentFragment = M1Fragment()
        //R.id.clearPCard.isEnabled = true
        //R.id.clearPCard.isVisible = true
        navController = findNavController(R.id.navHostFragment)

        setOnClickListeners()
        toneCount = SettingsActivity.tCnt
        importPrefs()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_BUTTON)
        }
        val timerView = binding.timerView
    }

    private fun setOnClickListeners() {
        with(binding) {
            m1.setOnClickListener(this@MainActivity)
            m2.setOnClickListener(this@MainActivity)
            m3.setOnClickListener(this@MainActivity)
            m4.setOnClickListener(this@MainActivity)
            quick.setOnClickListener(this@MainActivity)
            settings.setOnClickListener(this@MainActivity)
            power.setOnClickListener {
                if (bright == 0){
                    bright = 1
                    power.setText("")
                    turnOffScreenBrightness()
                    resizeButtonToFullScreen(power)
                    return@setOnClickListener                }
                if (bright==1){
                    bright = 0
                    power.setText("Power Save")
                    restoreScreenBrightness()
                    resizeButtonToOG(power)
                    return@setOnClickListener
                }
            }
        }
    }
    private val timing: Runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000)
            minutes = seconds / 60
            seconds = seconds % 60
            milliSeconds = (updateTime % 1000)

            // Assuming timerView is a TextView
            val formattedTime = MessageFormat.format(
                "{0}:{1}:{2}", minutes, java.lang.String.format(
                    Locale.getDefault(), "%02d", seconds
                ), java.lang.String.format(Locale.getDefault(), "%03d", milliSeconds)
            )
            // Set the formatted time to the TextView
            // Replace R.id.timerView with the actual ID of your TextView
            //findViewById<TextView>(R.id.timerView).text = formattedTime
            binding.timerView.text = formattedTime

            handler.postDelayed(this, 0)
        }
    }
    fun startTiming() {
        handler.post(timing)
        startTime = SystemClock.uptimeMillis()
        timeBuff = 0L
    }
    fun stopTiming() {
        timeBuff += millisecondTime
        handler.removeCallbacks(timing)
    }
    private fun applyKeepScreenOnFlag(stayAwake: Boolean) {
        if (stayAwake) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.m1 -> {
                table = "m1"
                setOnClickListeners()
                currentFragment = M1Fragment()
                setSelection(binding.m1)
                navController.navigate(R.id.m1Fragment)
            }
            R.id.m2 -> {
                table = "m2"
                setOnClickListeners()
                currentFragment = M2Fragment()
                setSelection(binding.m2)
                navController.navigate(R.id.m2Fragment)
            }
            R.id.m3 -> {
                table = "m3"
                setOnClickListeners()
                currentFragment = M3Fragment()
                setSelection(binding.m3)
                navController.navigate(R.id.m3Fragment)
            }
            R.id.m4 -> {
                table = "m4"
                setOnClickListeners()
                currentFragment = M4Fragment()
                setSelection(binding.m4)
                navController.navigate(R.id.m4Fragment)
            }
            R.id.quick -> {
                table = "quick"
                currentFragment = null
                setSelection(binding.quick)
                navController.navigate(R.id.quickFragment)
            }
            R.id.settings -> {
                currentFragment = null
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
            R.id.power -> {
                //turnOffScreenBrightness()
            }
        }
    }
    private fun setSelection(selectedView: TextView) {
        binding.m1.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m2.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m3.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.m4.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        binding.quick.background = ContextCompat.getDrawable(this, R.drawable.text_view)
        selectedView.background = ContextCompat.getDrawable(this, R.drawable.text_view_selected)

    }
    override fun onResume() {
        super.onResume()
        importPrefs()

    }
    private fun turnOffScreenBrightness() {
        // Store the original brightness value
        originalBrightness = window.attributes.screenBrightness
        addDarkOverlay()
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(window.attributes)
            //screenBrightness = 0.0f
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF
        }
        window.attributes = layoutParams
    }
    private fun restoreScreenBrightness() {
        // Restore the original brightness value
        removeDarkOverlay()
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(window.attributes)
            screenBrightness = originalBrightness
        }
        window.attributes = layoutParams
    }
    private fun addDarkOverlay() {
        darkOverlay = View(this).apply {
            // Set layout parameters for the dark overlay to match the entire screen
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Set background color to semi-transparent black (#80000000)
            setBackgroundColor(Color.parseColor("#95000000"))
        }

        // Add the dark overlay to the root view of the activity if darkOverlay is not null
        darkOverlay?.let { darkOverlay ->
            val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(darkOverlay)
        }
    }
    private fun removeDarkOverlay() {
        darkOverlay?.let { darkView ->
            val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            if (rootView != null) {
               rootView.removeView(darkView)
            } else {}
        }
    }
    private fun resizeButtonToFullScreen(button: Button) {


        // Get the dimensions of the screen
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay

        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        // Capture initial button size
        if (initialWidth == 0 && initialHeight == 0) {
            initialWidth = button.width
            println("initialWidth: $initialWidth")
            initialHeight = button.height
            println("initialHeight: $initialHeight")
        }
        val layoutParams = ViewGroup.LayoutParams(screenWidth, screenHeight)
        button.layoutParams = layoutParams
        // Make the button transparent
        button.background.alpha = 0

        // Ensure the button is clickable
        button.isClickable = true

    }
    private fun resizeButtonToOG(button: Button) {
        button.setBackgroundColor(Color.parseColor("#818589"))
        button.background.alpha = 255
        // Set text
        button.text = getString(R.string.power)

        // Set text color
        button.setTextColor(ContextCompat.getColor(this, R.color.black))

        // Set layout parameters to wrap_content
        button.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        button.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        button.requestLayout()

        // If the button was initially placed relative to other views, reset its constraints
        val layoutParams = button.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = R.id.navHostFragment // Reset top constraint if necessary
        button.layoutParams = layoutParams
    }
    fun importPrefs() {
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val stayAwake = sharedPref.getBoolean("awake", false)
        val darkMode = sharedPref.getBoolean("dark", false)
        val timerDisplay = sharedPref.getBoolean("timerD",false)
        val power = findViewById<TextView>(R.id.power)
        //set stay awake for the phone
        applyKeepScreenOnFlag(stayAwake)
        if (stayAwake == true) {
            power.visibility = View.VISIBLE
        }
        if (stayAwake == false){
            power.visibility = View.INVISIBLE
        }
        // Set the default night mode based on the user's preference
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        if (timerDisplay != null) {
            when {
                timerDisplay == true -> {
                    timeDisplay = true
                    binding.timerView.visibility = View.VISIBLE
                }
                timerDisplay == false -> {
                    timeDisplay = false
                    binding.timerView.visibility = View.GONE
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            when (val currentFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment)?.childFragmentManager?.fragments?.first()) {
                is M1Fragment -> {
                    currentFragment.onStartClick()
                }

                is M2Fragment -> {
                    currentFragment.onStartClick()
                }

                is M3Fragment -> {
                    currentFragment.onStartClick()
                }

                is M4Fragment -> {
                    currentFragment.onStartClick()
                }

                is QuickFragment -> {
                    currentFragment.startCountDown()
                }
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

}

