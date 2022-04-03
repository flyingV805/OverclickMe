package kz.flyingv.overclickme

import android.animation.LayoutTransition
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.flyingv.overclickme.databinding.ActivityMainBinding

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

        startBattle()
    }

    private fun showWinDialog(){
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
            showWinDialog()
        }else if(battleState >= 0.9){
            showWinDialog()
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