package com.demo.movingtarget.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.os.SystemClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.demo.movingtarget.ui.M1Fragment
import com.demo.movingtarget.ui.M2Fragment
import com.demo.movingtarget.ui.M3Fragment
import com.demo.movingtarget.ui.M4Fragment
import com.demo.movingtarget.databinding.ActivityMainBinding
import java.text.MessageFormat
import java.util.Locale
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentM1Binding
import com.demo.movingtarget.utils.PreferenceHelper
import com.demo.movingtarget.utils.PreferenceHelper.get
import com.demo.movingtarget.utils.PreferenceHelper.set
import com.demo.movingtarget.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class M1Fragment : Fragment(), View.OnClickListener, TextWatcher {

    private lateinit var preference: SharedPreferences
    private var selectedRadioBtnID: Int = 0
    private var soundId = 0
    private lateinit var sp: SoundPool
    private var soundIdEnd = 0
    private lateinit var spEnd: SoundPool
    private var audioAttributes: AudioAttributes? = null
    private var timerValue: Double = 0.0
    private lateinit var countDownTimer: CountDownTimer
    lateinit var binding: FragmentM1Binding
    private var currentView: View? = null
    lateinit var clickAudio: MediaPlayer
    lateinit var clickAudio1: MediaPlayer
    lateinit var endMP: MediaPlayer
    lateinit var endMP1: MediaPlayer
    lateinit var session: SessionManager
    private var tCount: String? = null
    private var tPattern: String? = null
    private var tSelection: String? = null
    private var tTwoSelection: String? = null
    private var stayAwake: Boolean? = false
    private var darkMode: Boolean? = false
    var startTime = System.currentTimeMillis()
    private var timer = ""
    private var alarmTimer = ""
    private var playing = 0
    val handler = Handler()
    var setSound = false

    private var tCnt = 2//default of 2 tones
    private var tPtrn = 1//default of all same sound
    private var tSel= 2131886088//default value of short click
    private var tTwoSel = 2131886088//default value of short click

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        preference = PreferenceHelper.customPrefs(requireContext(), findNavController().currentDestination?.id.toString())

        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_m1, container, false)
            binding = FragmentM1Binding.bind(currentView!!)
            init()
            setOnClickListener()
            textWatcher()
            textChangeListener()


            binding.playerTable.p1Time.setText(preference.get("p1", ""))
            binding.playerTable.p2Time.setText(preference.get("p2", ""))
            binding.playerTable.p3Time.setText(preference.get("p3", ""))
            binding.playerTable.p4Time.setText(preference.get("p4", ""))
            binding.playerTable.p5Time.setText(preference.get("p5", ""))
            binding.playerTable.p6Time.setText(preference.get("p6", ""))
            binding.playerTable.p1Name.setText(preference.get("p1Name", ""))
            binding.playerTable.p2Name.setText(preference.get("p2Name", ""))
            binding.playerTable.p3Name.setText(preference.get("p3Name", ""))
            binding.playerTable.p4Name.setText(preference.get("p4Name", ""))
            binding.playerTable.p5Name.setText(preference.get("p5Name", ""))
            binding.playerTable.p6Name.setText(preference.get("p6Name", ""))

        }
        retrievePref()
        updatePreferences(tCount, tPattern, tSelection, tTwoSelection)

        return currentView!!
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioButtonId =  preference.get("currentSelectedID", 0)
        if (radioButtonId!=0){

            val radioButton = currentView?.findViewById<RadioButton>(radioButtonId)
            radioButton?.performClick()
        }
        val editTextToRadioMap = mapOf(
            binding.playerTable.p1Time to binding.playerTable.p1RadioBtn,
            binding.playerTable.p2Time to binding.playerTable.p2RadioBtn,
            binding.playerTable.p3Time to binding.playerTable.p3RadioBtn,
            binding.playerTable.p4Time to binding.playerTable.p4RadioBtn,
            binding.playerTable.p5Time to binding.playerTable.p5RadioBtn,
            binding.playerTable.p6Time to binding.playerTable.p6RadioBtn,
            binding.playerTable.p1Name to binding.playerTable.p1RadioBtn,
            binding.playerTable.p2Name to binding.playerTable.p2RadioBtn,
            binding.playerTable.p3Name to binding.playerTable.p3RadioBtn,
            binding.playerTable.p4Name to binding.playerTable.p4RadioBtn,
            binding.playerTable.p5Name to binding.playerTable.p5RadioBtn,
            binding.playerTable.p6Name to binding.playerTable.p6RadioBtn,
        )

        for ((editText, radioBtn) in editTextToRadioMap) {
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    // Uncheck all other radio buttons
                    for ((otherEditText, otherRadioBtn) in editTextToRadioMap) {
                        if (editText != otherEditText) {
                            otherRadioBtn.isChecked = false
                        }
                    }
                    // Check the selected radio button
                    radioBtn.isChecked = true

                    // Perform click action on the radio button
                    radioBtn.performClick()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        storeInPreference()
    }
    override fun onDestroy() {
        super.onDestroy()
        // Save preferences when the activity is destroyed
        storeInPreference()
    }
    override fun onPause() {
        super.onPause()
        // Save preferences when the activity is paused
        storeInPreference()
    }

    private fun storeInPreference() {
        preference["p1"] = binding.playerTable.p1Time.text.toString()
        preference["p2"] = binding.playerTable.p2Time.text.toString()
        preference["p3"] = binding.playerTable.p3Time.text.toString()
        preference["p4"] = binding.playerTable.p4Time.text.toString()
        preference["p5"] = binding.playerTable.p5Time.text.toString()
        preference["p6"] = binding.playerTable.p6Time.text.toString()
        preference["p1Name"] = binding.playerTable.p1Name.text.toString()
        preference["p2Name"] = binding.playerTable.p2Name.text.toString()
        preference["p3Name"] = binding.playerTable.p3Name.text.toString()
        preference["p4Name"] = binding.playerTable.p4Name.text.toString()
        preference["p5Name"] = binding.playerTable.p5Name.text.toString()
        preference["p6Name"] = binding.playerTable.p6Name.text.toString()
        preference["currentSelectedID"] = selectedRadioBtnID
    }

    private fun textChangeListener() {
        binding.playerTable.p1Time.addTextChangedListener(this)
        binding.playerTable.p2Time.addTextChangedListener(this)
        binding.playerTable.p3Time.addTextChangedListener(this)
        binding.playerTable.p4Time.addTextChangedListener(this)
        binding.playerTable.p5Time.addTextChangedListener(this)
        binding.playerTable.p6Time.addTextChangedListener(this)
    }

    fun setCountCountTimer(time: String) {
        timerValue = time.toDouble()
        Log.e("TAG", "setCountCountTimer: $timerValue")
        val interval = 200L
        countDownTimer = object : CountDownTimer((timerValue.toDouble() * 1000).toLong(), interval) {

            override fun onTick(millisUntilFinished: Long) {
                Log.e("TAG", "onTick: $millisUntilFinished")
            }

            override fun onFinish() {
                Log.e("TAG", "onFinish: ")
                if (session.endTimerSound) {

                    if (timerValue < 1000) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(200)
                            spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                        }
                    } else {
                        spEnd.play(soundIdEnd, 500F, 500F, 1, 0, 1f)
                    }
                }
                binding.playerTable.startTimer.isEnabled = true
                binding.playerTable.startTimer.isVisible = true



                // logic to handle completion could go here
            }
        }


    }

    private fun textWatcher() {
        binding.playerTable.p1Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p1Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p1Time.text.toString().toDouble() /*  /100  */
                        binding.playerTable.p1Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p2Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p2Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p2Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p2Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p3Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p3Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p3Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p3Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p4Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p4Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p4Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p4Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p5Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p5Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p5Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p5Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

        binding.playerTable.p6Time.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event -> // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    if (binding.playerTable.p6Time.text.isNotEmpty()) {
                        val newValue =
                            binding.playerTable.p6Time.text.toString().toDouble() /* /100 */
                        binding.playerTable.p6Time.setText(newValue.toString())

                    }
                    return@OnEditorActionListener true
                }
                // Return true if you have consumed the action, else false.
                false
            })

    }
    override fun onResume() {
        super.onResume()
        retrievePref()
        updatePreferences(tCount, tPattern, tSelection, tTwoSelection)
    }
    private fun init() {
        session = SessionManager(requireContext())
        clickAudio = MediaPlayer.create(requireActivity().applicationContext, R.raw.u_click)
        clickAudio1 = MediaPlayer.create(requireActivity().applicationContext, R.raw.u_click)
        endMP = MediaPlayer.create(context, R.raw.just_like_that)
        endMP1 = MediaPlayer.create(context, R.raw.just_like_that)
        currentView?.setOnKeyListener { _, keyCode, _ ->
            Log.d("MyFragment", "Key event received: $keyCode")
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("MyFragment", "Volume button pressed")
                onStartClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        sp = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        spEnd = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        with(binding.playerTable) {
            p1RadioBtn.setOnClickListener(this@M1Fragment)
            p2RadioBtn.setOnClickListener(this@M1Fragment)
            p3RadioBtn.setOnClickListener(this@M1Fragment)
            p4RadioBtn.setOnClickListener(this@M1Fragment)
            p5RadioBtn.setOnClickListener(this@M1Fragment)
            p6RadioBtn.setOnClickListener(this@M1Fragment)
            clearPCard.setOnLongClickListener {
                binding.playerTable.apply {
                    p1Name.text = null
                    p1Time.text = null
                    p2Time.text = null
                    p2Name.text = null
                    p3Time.text = null
                    p3Name.text = null
                    p4Time.text = null
                    p4Name.text = null
                    p5Time.text = null
                    p5Name.text = null
                    p6Time.text = null
                    p6Name.text = null
                }
                true
            }
            startTimer.setOnClickListener {
                onStartClick()
            }

            startTimer.setOnTouchListener { _, event ->
                when (event.action) {

                    MotionEvent.ACTION_BUTTON_PRESS,
                    MotionEvent.ACTION_DOWN -> {
                        onStartClick()
                        true
                    }

                    else -> false
                }
            }
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.p1RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p1RadioBtn,
                    binding.playerTable.p1Time.text.toString()
                )
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view)
            }
            R.id.p2RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p2RadioBtn,
                    binding.playerTable.p2Time.text.toString()
                )
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view)
            }
            R.id.p3RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p3RadioBtn,
                    binding.playerTable.p3Time.text.toString()
                )
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view)
            }
            R.id.p4RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p4RadioBtn,
                    binding.playerTable.p4Time.text.toString()
                )
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view)
            }
            R.id.p5RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p5RadioBtn,
                    binding.playerTable.p5Time.text.toString()
                )
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view)
            }
            R.id.p6RadioBtn -> {
                setRadioSelection(
                    binding.playerTable.p6RadioBtn,
                    binding.playerTable.p6Time.text.toString()
                )
                binding.playerTable.p6Time.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p6Name.setBackgroundResource(R.drawable.text_view_radio_selection)
                binding.playerTable.p2Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p2Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p3Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p4Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p5Name.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Time.setBackgroundResource(R.drawable.text_view)
                binding.playerTable.p1Name.setBackgroundResource(R.drawable.text_view)
            }
        }
    }

    fun onStartClick() {
        startTime = System.currentTimeMillis()
        Log.e("TAG", "onStartClick")
        validateAndStartTimer("", "0", 1)
        (activity as? MainActivity)?.startTiming()
        return
    }


    private fun validateAndStartTimer(pName: String, pTime: String, player: Int) {
        if (pTime.isNotEmpty()) {
            var x = 1
            var count = tCnt
            startTime = System.currentTimeMillis()
            var endTime = System.currentTimeMillis()
            var holdUp = endTime - startTime
            var continueLoop = true
            if (playing == 0) {
                playing = 1
                MainActivity.continueLoop = true
                binding.playerTable.startTimer.text = "STOP"
                binding.playerTable.startTimer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                //val timerValue = timer.toDoubleOrNull() ?: 0.0
                endTime = System.currentTimeMillis()
                holdUp = endTime - startTime
                //below can switch between removing the delay or not holdUp is the calculated processing time
                val delay = (timerValue * 1000 - holdUp).toLong()
                //val delay = (timerValue * 1000).toLong()
                println("the delay was: $holdUp")
                count = tCnt-1
                countDownTimer = object : CountDownTimer(delay * count, delay) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Implement onTick if needed
                    }

                    override fun onFinish() {
                        // Implement onFinish if needed
                        binding.playerTable.startTimer.text = "START"
                        binding.playerTable.startTimer.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.Gunmetal_Gray
                            )
                        )
                    }
                }
                countDownTimer?.start()
                sp.play(soundId, 1F, 1F, 1, 0, 1f)
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (!MainActivity.continueLoop) return
                        when (tPtrn) {
                            1 -> {
                                (activity as? MainActivity)?.stopTiming()
                                sp.play(soundId, 1F, 1F, 1, 0, 1f)
                            }
                            2 -> {
                                if (x == 1) {
                                    (activity as? MainActivity)?.stopTiming()
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x = 0
                                }
                                else if (x == 0) {
                                    (activity as? MainActivity)?.stopTiming()
                                    sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    x = 1
                                }
                            }

                            3 -> {
                                if (x == 0){
                                    (activity as? MainActivity)?.stopTiming()
                                    sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    x++
                                }
                                else if (x == 1){
                                    (activity as? MainActivity)?.stopTiming()
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x++
                                }
                                else if (x == 2){
                                    (activity as? MainActivity)?.stopTiming()
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x=0
                                }
                            }

                            4 -> {
                                if (x == 1 || x == 2){
                                    (activity as? MainActivity)?.stopTiming()
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x++
                                }
                                else if (x == 3){
                                    (activity as? MainActivity)?.stopTiming()
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x = 0
                                }
                                else if (x == 0){
                                    (activity as? MainActivity)?.stopTiming()
                                    sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    x++
                                }
                            }
                        }

                        count--
                        if (count > 0 && MainActivity.continueLoop) {
                            handler.postDelayed(this, delay)
                        }
                        if (count ==0){
                            playing = 0
                            MainActivity.continueLoop = false
                            binding.playerTable.startTimer.text = "START"
                            binding.playerTable.startTimer.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.Gunmetal_Gray
                                )
                            )
                        }
                    }
                }, delay)

            } else if (playing == 1) {
                playing = 0
                count = 0
                MainActivity.continueLoop = false

                binding.playerTable.startTimer.text = "START"
                binding.playerTable.startTimer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.Gunmetal_Gray
                    )
                )
                countDownTimer?.cancel()
                handler.removeCallbacksAndMessages(null)
                if (sp != null || spEnd != null) {
                    releaseSoundPools()
                    setSound = true
                }

                initSoundPools()
                declareAudio()
            }

        }
    }

    private fun releaseSoundPools() {
        sp?.release()
        spEnd?.release()
    }
    private fun initSoundPools() {
    audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()

    sp = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(audioAttributes)
        .build()

    spEnd = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(audioAttributes)
        .build()
}
    fun declareAudio(){
        soundId = sp.load(requireActivity(),  tSel, 1)
        soundIdEnd = spEnd.load(requireActivity(), tTwoSel, 1)
        }

    suspend fun one(): Int {
            Thread.sleep(timerValue.toLong() * 1000)
            delay(timerValue.toLong() * 1000)

            return 1
        }

    private fun setRadioSelection(selectedRadioBtn: RadioButton, time: String) {
            with(binding.playerTable) {
                p1RadioBtn.isChecked = false
                p2RadioBtn.isChecked = false
                p3RadioBtn.isChecked = false
                p4RadioBtn.isChecked = false
                p5RadioBtn.isChecked = false
                p6RadioBtn.isChecked = false
            }
            selectedRadioBtnID = selectedRadioBtn.id
            selectedRadioBtn.isChecked = true
            if (time.isNotEmpty())
                setCountCountTimer(time)
        }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
    private fun retrievePref(){
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        tCount = sharedPref.getString("count",null)
        tPattern = sharedPref.getString("pattern",null)
        tSelection = sharedPref.getString("selection",null)
        tTwoSelection = sharedPref.getString("selectionTwo",null)
        stayAwake = sharedPref.getBoolean("awake",false)
        darkMode = sharedPref.getBoolean("dark",false)
    }
    fun updatePreferences(tCount: String?, tPattern: String?, tSelection: String?, tTwoSelection: String?) {
        if (tCount != null) {
            when {
                tCount.startsWith("N") -> {
                    tCnt = 99999
                }
                tCount.startsWith("Tw") -> {
                    tCnt = 2
                }
                tCount.isBlank() -> {
                    tCnt = 2
                }
                tCount.startsWith("Th") -> {
                    tCnt = 3
                }
                tCount.startsWith("F") -> {
                    tCnt = 4
                }
                tCount.startsWith("Si") -> {
                    tCnt = 6
                }
                tCount.startsWith("E") -> {
                    tCnt = 8
                }
                tCount.startsWith("Se") -> {
                    tCnt = 2
                }
            }
        }

        if (tPattern != null) {
            when {
                tPattern.startsWith("Sel") -> {
                    tPtrn = 1
                }
                tPattern.startsWith("1111") -> {
                    tPtrn = 1
                }
                tPattern.startsWith("1010") -> {
                    tPtrn = 2
                }
                tPattern.startsWith("1001") -> {
                    tPtrn = 3
                }
                tPattern.startsWith("10001") -> {
                    tPtrn = 4
                }
            }
        }

        if (tSelection != null) {
            when {
                tSelection.startsWith("Sel") -> {
                    tSel = R.raw.short_click
                }
                tSelection.startsWith("b") -> {
                    tSel = R.raw.bubble_click
                }
                tSelection.startsWith("s") -> {
                    tSel = R.raw.short_click
                }
                tSelection.startsWith("lou") -> {
                    tSel = R.raw.loud_click
                }
                tSelection.startsWith("lon") -> {
                    tSel = R.raw.long_beep
                }
            }
        }

        if (tTwoSelection != null) {
            when {
                tTwoSelection.startsWith("Sel") -> {
                    tTwoSel = R.raw.short_click
                }
                tTwoSelection.startsWith("b") -> {
                    tTwoSel = R.raw.bubble_click
                }
                tTwoSelection.startsWith("s") -> {
                    tTwoSel = R.raw.short_click
                }
                tTwoSelection.startsWith("lou") -> {
                    tTwoSel = R.raw.loud_click
                }
                tTwoSelection.startsWith("lon") -> {
                    tTwoSel = R.raw.long_beep
                }
            }
        }
        declareAudio()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {

        if (p0?.isNotEmpty() == true) {
            if (p0.length == 1 && p0[0].toString() == ".") {
                if (p0.hashCode() == binding.playerTable.p1Time.text.hashCode()) {
                    binding.playerTable.p1Time.setText("0.")
                    binding.playerTable.p1Time.setSelection(binding.playerTable.p1Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p2Time.text.hashCode()) {
                    binding.playerTable.p2Time.setText("0.")
                    binding.playerTable.p2Time.setSelection(binding.playerTable.p2Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p3Time.text.hashCode()) {
                    binding.playerTable.p3Time.setText("0.")
                    binding.playerTable.p3Time.setSelection(binding.playerTable.p3Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p4Time.text.hashCode()) {
                    binding.playerTable.p4Time.setText("0.")
                    binding.playerTable.p4Time.setSelection(binding.playerTable.p4Time.text.length)
                } else if (p0.hashCode() == binding.playerTable.p5Time.text.hashCode()) {
                    binding.playerTable.p5Time.setText("0.")
                    binding.playerTable.p5Time.setSelection(binding.playerTable.p5Time.text.length)
                } else {
                    binding.playerTable.p6Time.setText("0.")
                    binding.playerTable.p6Time.setSelection(binding.playerTable.p6Time.text.length)

                }
                setCountCountTimer("0.0")
                return
            }

            setCountCountTimer(p0.toString())
        }
    }



}