package com.demo.movingtarget.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentM1Binding
import com.demo.movingtarget.databinding.FragmentM3Binding


class M3Fragment : Fragment(),View.OnClickListener {

    lateinit var binding: FragmentM3Binding
    private var currentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_m3, container, false)
            binding = FragmentM3Binding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }
    companion object {
        fun clearEditTextField(binding: FragmentM3Binding) {
            binding.playerTable.p1Time.text = null
            binding.playerTable.p1Name.text = null
            binding.playerTable.p2Time.text = null
            binding.playerTable.p2Name.text = null
            binding.playerTable.p3Time.text = null
            binding.playerTable.p3Name.text = null
            binding.playerTable.p4Time.text = null
            binding.playerTable.p4Name.text = null
            binding.playerTable.p5Time.text = null
            binding.playerTable.p5Name.text = null
            binding.playerTable.p6Time.text = null
            binding.playerTable.p6Name.text = null
        }
    }

    private fun init() {
        currentView!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                onStartClick()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        with(binding.playerTable) {
            p1RadioBtn.setOnClickListener(this@M3Fragment)
            p2RadioBtn.setOnClickListener(this@M3Fragment)
            p3RadioBtn.setOnClickListener(this@M3Fragment)
            p4RadioBtn.setOnClickListener(this@M3Fragment)
            p5RadioBtn.setOnClickListener(this@M3Fragment)
            p6RadioBtn.setOnClickListener(this@M3Fragment)
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
            startTimer.setOnTouchListener { _, event ->
                when (event.action) {
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
            R.id.p1RadioBtn -> setRadioSelection(binding.playerTable.p1RadioBtn)
            R.id.p2RadioBtn -> setRadioSelection(binding.playerTable.p2RadioBtn)
            R.id.p3RadioBtn -> setRadioSelection(binding.playerTable.p3RadioBtn)
            R.id.p4RadioBtn -> setRadioSelection(binding.playerTable.p4RadioBtn)
            R.id.p5RadioBtn -> setRadioSelection(binding.playerTable.p5RadioBtn)
            R.id.p6RadioBtn -> setRadioSelection(binding.playerTable.p6RadioBtn)

        }
    }

    fun onStartClick() {


        if (binding.playerTable.p1RadioBtn.isChecked) {
            val p1Name = binding.playerTable.p1Name.text.toString()
            val p1Time = binding.playerTable.p1Time.text.toString()
            validateAndStartTimer(p1Name, p1Time, 1)
        } else if (binding.playerTable.p2RadioBtn.isChecked) {
            val p2Name = binding.playerTable.p2Name.text.toString()
            val p2Time = binding.playerTable.p2Time.text.toString()
            validateAndStartTimer(p2Name, p2Time, 2)
        } else if (binding.playerTable.p3RadioBtn.isChecked) {
            val p3Name = binding.playerTable.p3Name.text.toString()
            val p3Time = binding.playerTable.p3Time.text.toString()
            validateAndStartTimer(p3Name, p3Time, 3)
        } else if (binding.playerTable.p4RadioBtn.isChecked) {
            val p4Name = binding.playerTable.p4Name.text.toString()
            val p4Time = binding.playerTable.p4Time.text.toString()
            validateAndStartTimer(p4Name, p4Time, 4)
        } else if (binding.playerTable.p5RadioBtn.isChecked) {
            val p5Name = binding.playerTable.p5Name.text.toString()
            val p5Time = binding.playerTable.p5Time.text.toString()
            validateAndStartTimer(p5Name, p5Time, 5)
        } else if (binding.playerTable.p6RadioBtn.isChecked) {
            val p6Name = binding.playerTable.p6Name.text.toString()
            val p6Time = binding.playerTable.p6Time.text.toString()
            validateAndStartTimer(p6Name, p6Time, 6)
        }
    }

    private fun validateAndStartTimer(pName: String, pTime: String, player: Int) {
        if (pName.isEmpty() || pTime.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "please enter Player $player Details",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        binding.playerTable.startTimer.background.setTint(ContextCompat.getColor(requireContext(),R.color.red_lite))
        binding.playerTable.startTimer.isEnabled = false

        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.u_click)
        mp.start()

        val timer = pTime.toDouble()
        val countDownTimer = object : CountDownTimer(timer.toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.click_sound)
                mp.start()
                binding.playerTable.startTimer.background.setTint(ContextCompat.getColor(requireContext(),R.color.red))
                binding.playerTable.startTimer.isEnabled = true
            }
        }
        countDownTimer.start()
    }

    private fun setRadioSelection(selectedRadioBtn: RadioButton) {
        with(binding.playerTable) {
            p1RadioBtn.isChecked = false
            p2RadioBtn.isChecked = false
            p3RadioBtn.isChecked = false
            p4RadioBtn.isChecked = false
            p5RadioBtn.isChecked = false
            p6RadioBtn.isChecked = false
        }
        selectedRadioBtn.isChecked = true
    }
}