package kz.flyingv.overclickme

import android.animation.LayoutTransition
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.Guideline
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
            binding.battleLine.setGuidelinePercent(battleState)
        }

        binding.player1.setOnClickListener {
            battleState -= 0.02f
            binding.battleLine.setGuidelinePercent(battleState)
        }

        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

    }

}