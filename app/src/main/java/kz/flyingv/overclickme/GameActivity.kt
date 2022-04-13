package kz.flyingv.overclickme

import android.animation.Animator
import android.animation.LayoutTransition
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.flyingv.overclickme.databinding.ActivityMainBinding
import kz.flyingv.overclickme.model.Player

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var battleState = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.player0.setOnClickListener {
            battleState += 0.02f
        }

        binding.player1.setOnClickListener {
            battleState -= 0.02f
        }

        binding.player0Settings.setOnClickListener {showSettings()}
        binding.player0Restart.setOnClickListener {restartBattle()}

        binding.player1Settings.setOnClickListener {showSettings()}
        binding.player1Restart.setOnClickListener {restartBattle()}

        initUI()
        startCountDown()
    }

    private fun initUI(){
        binding.player0.isClickable = false
        binding.player1.isClickable = false
        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.animationView.speed = 1.6f
        binding.animationView.setMaxProgress(0.78f)

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
            }
            Player.TWO -> {
                binding.player1Win.visibility = View.VISIBLE
                binding.player1Win.playAnimation()
                binding.player1Options.visibility = View.VISIBLE
            }
        }
    }

    private fun finishBattle(){
        if(battleState <= 0.1){
            showWinView(Player.TWO)
        }else if(battleState >= 0.9){
            showWinView(Player.ONE)
        }
    }

    private fun restartBattle(){
        battleState = 0.5f
        binding.battleLine.setGuidelinePercent(battleState)


        binding.player0Win.visibility = View.GONE
        binding.player0Win.cancelAnimation()
        binding.player0Options.visibility = View.GONE
        binding.player0Settings.hide()
        binding.player0Restart.hide()

        binding.player1Win.visibility = View.GONE
        binding.player1Win.cancelAnimation()
        binding.player1Options.visibility = View.GONE
        binding.player1Settings.hide()
        binding.player1Restart.hide()

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

        dialog.show()

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
            binding.player0Settings.show()
            binding.player0Restart.show()
        }

    }

    inner class Player1AnimationListener: Animator.AnimatorListener{

        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}

        override fun onAnimationEnd(p0: Animator?) {
            binding.player1Settings.show()
            binding.player1Restart.show()
        }

    }

}