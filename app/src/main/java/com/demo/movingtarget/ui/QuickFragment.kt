package com.demo.movingtarget.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentQuickBinding
import com.demo.movingtarget.utils.PreferenceHelper
import com.demo.movingtarget.utils.PreferenceHelper.get
import com.demo.movingtarget.utils.PreferenceHelper.set
import java.text.DecimalFormat


class QuickFragment : Fragment(), View.OnClickListener {

    private lateinit var preference: SharedPreferences
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var binding: FragmentQuickBinding
    private var currentView: View? = null
    private var timer = ""
    private var alarmTimer = ""
    private var soundId = 0
    private var playing = 0
    private lateinit var sp: SoundPool
    private var soundIdEnd = 0
    private lateinit var spEnd: SoundPool
    private var decimalPressed = false
    private var audioAttributes: AudioAttributes? = null
    private var tCount: String? = null
    private var tPattern: String? = null
    private var tSelection: String? = null
    private var tTwoSelection: String? = null
    private var stayAwake: Boolean? = false
    private var darkMode: Boolean? = false
    var setSound = false
    var startTime = System.currentTimeMillis()
    private var tCnt = 2//default of 2 tones
    private var tPtrn = 1//default of all same sound
    private var tSel= 2131886088//default value of short click
    private var tTwoSel = 2131886088//default value of short click

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        preference = PreferenceHelper.customPrefs(
            requireContext(),
            findNavController().currentDestination?.id.toString()
        )

        if (currentView == null) {

            currentView = inflater.inflate(R.layout.fragment_quick, container, false)
            binding = FragmentQuickBinding.bind(currentView!!)
            init()
            setOnClickListener()

            preference["time", ""].let {
                if (it.isNotEmpty() && it != "0") {
                    timer = it
                    setTimer()
                }
            }

        }
        retrievePref()
        updatePreferences(tCount, tPattern, tSelection, tTwoSelection)
        return currentView!!
    }

    override fun onDestroyView() {
        super.onDestroyView()

        storeInPreference()
    }

    private fun storeInPreference() {
        preference["time"] = timer
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
    override fun onResume() {
        super.onResume()
        // Set the focusable in touch mode to ensure the fragment can receive key events
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        retrievePref()
        updatePreferences(tCount, tPattern, tSelection, tTwoSelection)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        with(binding) {
            one.setOnClickListener(this@QuickFragment)
            two.setOnClickListener(this@QuickFragment)
            three.setOnClickListener(this@QuickFragment)
            four.setOnClickListener(this@QuickFragment)
            five.setOnClickListener(this@QuickFragment)
            six.setOnClickListener(this@QuickFragment)
            seven.setOnClickListener(this@QuickFragment)
            eight.setOnClickListener(this@QuickFragment)
            nine.setOnClickListener(this@QuickFragment)
            zero.setOnClickListener(this@QuickFragment)
            dot.setOnClickListener(this@QuickFragment)
            clear.setOnClickListener(this@QuickFragment)
//            startTimer.setOnClickListener(this@QuickFragment)

            startTimer.setOnTouchListener { _, event ->
                //startTime = System.currentTimeMillis()
                when (event.action) {

                    MotionEvent.ACTION_BUTTON_PRESS,
                    MotionEvent.ACTION_DOWN -> {
                        startCountDown()
                        (activity as? MainActivity)?.startTiming()

                        true
                    }

                    else -> false
                }
            }
        }
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
                    tTwoSel = R.raw.bubble_click//0
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

    private fun init() {
        //initSoundPools()
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
        // soundId = sp.load(requireActivity(),  MainActivity.audio[tSel], 1)
        //soundIdEnd = spEnd.load(requireActivity(), MainActivity.audio[tTwoSel], 1)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.one -> {
                timer = "${timer}1"
                setTimer()

            }
            R.id.two -> {
                timer = "${timer}2"
                Log.e("Data>>>>>", timer)
                setTimer()

            }
            R.id.three -> {
                timer = "${timer}3"
                setTimer()

            }
            R.id.four -> {
                timer = "${timer}4"
                setTimer()

            }
            R.id.five -> {
                timer = "${timer}5"
                setTimer()

            }
            R.id.six -> {
                timer = "${timer}6"
                setTimer()

            }
            R.id.seven -> {
                timer = "${timer}7"
                setTimer()

            }
            R.id.eight -> {
                timer = "${timer}8"
                setTimer()
            }
            R.id.nine -> {
                timer = "${timer}9"
                setTimer()
            }
            R.id.zero -> {
                timer = "${timer}0"
                setTimer()
            }
            R.id.dot -> {
                onDecimalButtonClick()
                if (timer.isBlank()){
                    timer = "${timer}0."
                }else if (!timer.contains('.')){
                    timer = "${timer}."
                }
                setTimer()
            }
            R.id.clear -> {
                timer = ""
                alarmTimer = ""
                binding.timerEdt.text = "0"
            }
            R.id.startTimer -> {


            }
        }
    }
    fun declareAudio(){
        soundId = sp.load(requireActivity(),  tSel, 1)
        soundIdEnd = spEnd.load(requireActivity(), tTwoSel, 1)
        }
    fun startCountDown() {
        var x = 1
        startTime = System.currentTimeMillis()
        var endTime = System.currentTimeMillis()
        var holdUp = endTime - startTime
        var count = tCnt
        var delayTwo: Long = 0
        val handler = Handler()
        var continueLoop = true
        if (playing == 0) {
            playing = 1
            MainActivity.continueLoop = true
            binding.startTimer.text = "STOP"
            binding.startTimer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            val timerValue = timer.toDoubleOrNull() ?: 0.0
            endTime = System.currentTimeMillis()
            holdUp = endTime - startTime
            val delay = (timerValue * 1000 - holdUp).toLong()
            println("the delay was: $holdUp")
            //delayTwo = delay - holdUp
            count = tCnt-1
            countDownTimer = object : CountDownTimer(delay * count, delay) {
                override fun onTick(millisUntilFinished: Long) {
                    // Implement onTick if needed
                }

                override fun onFinish() {
                    // Implement onFinish if needed
                    binding.startTimer.text = "START"
                    binding.startTimer.setBackgroundColor(
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
                                (activity as? MainActivity)?.stopTiming()
                                if (x == 1) {
                                    spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    x = 0
                                } else {
                                    sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    x = 1
                                }
                            }

                            3 -> {
                                (activity as? MainActivity)?.stopTiming()

                                when (x) {
                                    0 -> sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    1 -> spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    2 -> spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                }
                                x = (x + 1) % 3
                            }

                            4 -> {
                                (activity as? MainActivity)?.stopTiming()
                                when (x) {
                                    0 -> sp.play(soundId, 1F, 1F, 1, 0, 1f)
                                    1, 2 -> spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                    3 -> spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                                }
                                x = (x + 1) % 4
                            }
                        }

                        count--

                        if (count > 0 && MainActivity.continueLoop) {
                            handler.postDelayed(this, delay)
                        }
                        if (count ==0){
                            playing = 0
                            MainActivity.continueLoop = false
                            binding.startTimer.text = "START"
                            binding.startTimer.setBackgroundColor(
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
            binding.startTimer.text = "START"
            binding.startTimer.setBackgroundColor(
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
    // Function to initialize SoundPool instances
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
    // Function to release SoundPool instances
    private fun releaseSoundPools() {
        sp?.release()
        spEnd?.release()
    }
    private fun onDecimalButtonClick() {
        decimalPressed = true // Set the flag to true when the decimal button is pressed
        // Update UI or perform any other action as needed
    }

    private fun setTimer() {
        val seconds = timer.toDouble()
        val formatPattern = if (decimalPressed) {
            "0.00" // If decimal button is pressed, display two decimal places
        } else {
            "0" // Otherwise, display only the whole number
        }
        val formattedValue = DecimalFormat(formatPattern).format(seconds)
        alarmTimer = formattedValue.toString()
        binding.timerEdt.text = formattedValue.toString()
        Log.e("Data>>>", "setTimer:  alarm: ${alarmTimer.toDouble() * 1000}")

        // Create the countdown timer with the corrected milliseconds duration
        countDownTimer = object : CountDownTimer(((alarmTimer.toDouble() * 1000)+150).toLong(), 500) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("Data>>>", "millisecond: $millisUntilFinished")
            }

            override fun onFinish() {
                spEnd.play(soundIdEnd, 1F, 1F, 1, 0, 1f)
                binding.startTimer.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                binding.startTimer.isEnabled = true

            }
        }
    }
}

