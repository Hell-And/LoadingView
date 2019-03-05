package com.example.lixiang.loadingview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var flag: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        del_ball.setOnClickListener { gradientview.delBall() }
        add_ball.setOnClickListener { gradientview.addBall() }
        bt_load.setOnClickListener {
            if (flag) {
                triangleView.stop()
                bezierView.stop()
                circleView.stop()
                gradientview.stop()
                bt_load.text = "开始"
                flag = false
            } else {
                flag = true
                triangleView.start()
                bezierView.start()
                circleView.start()
                gradientview.start()
                bt_load.text = "完成"
            }

        }
    }
}
