package kz.flyingv.overclickme

import android.animation.Animator
import android.animation.LayoutTransition
import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.flyingv.overclickme.databinding.ActivityMainBinding
import kz.flyingv.overclickme.model.Player

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    private var battleState = 0.5f

    private var player0Color: Int = R.color.paletteDefaultPlayer0
    private var player1Color: Int = R.color.paletteDefaultPlayer1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setContentView(binding.root)

        binding.player0.setOnClickListener {
            battleState += 0.02f
        }

        binding.player1.setOnClickListener {
            battleState -= 0.02f
        }

        //binding.player0Settings.setOnClickListener {showSettings()}
        binding.player0Restart.setOnClickListener {restartBattle(Player.ONE)}

        //binding.player1Settings.setOnClickListener {showSettings()}
        binding.player1Restart.setOnClickListener {restartBattle(Player.TWO)}

        initUI()
        startCountDown()
    }

    private fun initUI(){
        binding.player0.isClickable = false
        binding.player1.isClickable = false
        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.animationView.speed = 1.6f
        binding.animationView.setMaxProgress(0.78f)

        player0Color = preferences.getInt("player0Color", R.color.paletteDefaultPlayer0)
        player1Color = preferences.getInt("player1Color", R.color.paletteDefaultPlayer1)

        binding.player0.setBackgroundColor( ContextCompat.getColor(this, player0Color) )
        binding.player1.setBackgroundColor( ContextCompat.getColor(this, player1Color) )

        binding.player0Restart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, player1Color))
        //binding.player0Settings.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, player1Color))

        binding.player1Restart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, player0Color))
        //binding.player1Settings.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, player1Color))

        binding.animationView.addAnimatorListener(StartAnimationListener())
        binding.player0Win.addAnimatorListener(Player0AnimationListener())
        binding.player1Win.addAnimatorListener(Player1AnimationListener())

    }

    private fun startCountDown(){
        binding.countdown.visibility = View.VISIBLE
        binding.animationView.playAnimation()
    }

    private fun showWinView(player: Player){
        when(player){
            Player.ONE -> {
                binding.player0Win.visibility = View.VISIBLE
                binding.player0Win.playAnimation()
                binding.player0Options.visibility = View.VISIBLE
                val player0Background = arrayOf(
                    ColorDrawable(ContextCompat.getColor(this, player0Color)),
                    ColorDrawable(ContextCompat.getColor(this, R.color.paletteBackground))
                )
                val player0BackgroundTransition = TransitionDrawable(player0Background)
                binding.player0.background = player0BackgroundTransition
                player0BackgroundTransition.startTransition(200)
            }
            Player.TWO -> {
                binding.player1Win.visibility = View.VISIBLE
                binding.player1Win.playAnimation()
                binding.player1Options.visibility = View.VISIBLE
                val player1Background = arrayOf(
                    ColorDrawable(ContextCompat.getColor(this, player1Color)),
                    ColorDrawable(ContextCompat.getColor(this, R.color.paletteBackground))
                )
                val player1BackgroundTransition = TransitionDrawable(player1Background)
                binding.player1.background = player1BackgroundTransition
                player1BackgroundTransition.startTransition(200)
            }
        }
    }

    private fun finishBattle(){
        binding.player0.isClickable = false
        binding.player1.isClickable = false
        if(battleState <= 0.1){
            showWinView(Player.TWO)
        }else if(battleState >= 0.9){
            showWinView(Player.ONE)
        }
    }

    private fun restartBattle(player: Player){
        battleState = 0.5f
        binding.battleLine.setGuidelinePercent(battleState)

        binding.player0Win.visibility = View.GONE
        binding.player0Win.cancelAnimation()
        binding.player0Options.visibility = View.GONE
        //binding.player0Settings.hide()
        binding.player0Restart.hide()

        binding.player1Win.visibility = View.GONE
        binding.player1Win.cancelAnimation()
        binding.player1Options.visibility = View.GONE
        //binding.player1Settings.hide()
        binding.player1Restart.hide()

        when(player){
            Player.ONE -> {
                val player0Background = arrayOf(
                    ColorDrawable(ContextCompat.getColor(this, R.color.paletteBackground)),
                    ColorDrawable(ContextCompat.getColor(this, player0Color))
                )
                val player0BackgroundTransition = TransitionDrawable(player0Background)
                binding.player0.background = player0BackgroundTransition
                player0BackgroundTransition.startTransition(200)
            }
            Player.TWO -> {
                val player1Background = arrayOf(
                    ColorDrawable(ContextCompat.getColor(this, R.color.paletteBackground)),
                    ColorDrawable(ContextCompat.getColor(this, player1Color))
                )
                val player1BackgroundTransition = TransitionDrawable(player1Background)
                binding.player1.background = player1BackgroundTransition
                player1BackgroundTransition.startTransition(200)
            }
        }

        startCountDown()
    }

    private fun startBattle(){
        binding.player0.isClickable = true
        binding.player1.isClickable = true
        var isPlaying = true
        lifecycleScope.launch {
            while (isPlaying){
                binding.battleLine.setGuidelinePercent(battleState)
                delay(33)
                if(battleState <= 0.1 || battleState >= 0.9){
                    isPlaying = false
                    finishBattle()
                }
            }
        }
    }

    private fun showSettings(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_settings)

        val colorSelector = dialog.findViewById<RadioGroup>(R.id.colorPalette)

        var newPlayer0Color = R.color.paletteDefaultPlayer0
        var newPlayer1Color = R.color.paletteDefaultPlayer0

        colorSelector.setOnCheckedChangeListener { _, paletteId ->
            when(paletteId){
                R.id.colorPaletteDefault -> {
                    newPlayer0Color = R.color.paletteDefaultPlayer0
                    newPlayer1Color = R.color.paletteDefaultPlayer1
                }
                R.id.colorPaletteAlt -> {
                    newPlayer0Color = R.color.paletteAltPlayer0
                    newPlayer1Color = R.color.paletteAltPlayer1

                }
                R.id.colorPaletteContrast -> {
                    newPlayer0Color = R.color.paletteContrastPlayer0
                    newPlayer1Color = R.color.paletteContrastPlayer1

                }
            }
            updateUIColorPalette(newPlayer0Color, newPlayer1Color)
        }

        dialog.setOnDismissListener {
            preferences.edit().putInt("player0Color", player0Color).putInt("player1Color", player1Color).apply()
        }

        dialog.show()

    }

    private fun updateUIColorPalette(newPlayer0Color: Int, newPlayer1Color: Int){
        val player0Colors = arrayOf(
            ColorDrawable(ContextCompat.getColor(this, player0Color)),
            ColorDrawable(ContextCompat.getColor(this, newPlayer0Color))
        )
        val player1Colors = arrayOf(
            ColorDrawable(ContextCompat.getColor(this, player1Color)),
            ColorDrawable(ContextCompat.getColor(this, newPlayer1Color))
        )

        val player0Transition = TransitionDrawable(player0Colors)
        binding.player0.background = player0Transition

        val player1Transition = TransitionDrawable(player1Colors)
        binding.player1.background = player1Transition


        player0Transition.startTransition(200)
        player1Transition.startTransition(200)

        player0Color = newPlayer0Color
        player1Color = newPlayer1Color

    }

    inner class StartAnimationListener: Animator.AnimatorListener{

        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}

        override fun onAnimationEnd(p0: Animator?) {
            binding.countdown.visibility = View.GONE
            startBattle()
        }

    }

    inner class Player0AnimationListener: Animator.AnimatorListener{

        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}

        override fun onAnimationEnd(p0: Animator?) {
            //binding.player0Settings.show()
            binding.player0Restart.show()
        }

    }

    inner class Player1AnimationListener: Animator.AnimatorListener{

        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}

        override fun onAnimationEnd(p0: Animator?) {
            //binding.player1Settings.show()
            binding.player1Restart.show()
        }

    }

}