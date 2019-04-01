package pers.lin.animations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pers.lin.linanimations.animations.RippleView;

public class Main2Activity extends AppCompatActivity {
    int i=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button = (Button)findViewById(R.id.button2);
        final RippleView rippleView = (RippleView)findViewById(R.id.iview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i%2==1)
                    rippleView.pause();
                else if(i%2==0)
                    rippleView.start();
                i++;

            }
        });
    }
}
