package kz.flyingv.overclickme

import android.animation.Animator
import android.animation.LayoutTransition
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
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

        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.animationView.speed = 1.6f
        binding.animationView.setMaxProgress(0.78f)

        startCountDown()
    }

    private fun startCountDown(){
        binding.animationView.playAnimation()
        binding.animationView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                binding.animationView.visibility = View.GONE
                startBattle()
            }
        })
    }

    private fun showWinView(player: Player){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_win_dialog)
        dialog.setCancelable(false)

        dialog.findViewById<AppCompatButton>(R.id.again).setOnClickListener {
            dialog.dismiss()
            restartBattle()
        }

        dialog.show()
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
        startBattle()
    }

    private fun startBattle(){
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

}