package jp.co.yahoo.sample.explicit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Main Activity
 *
 * @author Copyright (C) 2012 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button)findViewById(R.id.button_start);
        btn.setOnClickListener(this);
    }

	public void onClick(View v) {
    	if(v.getId() == R.id.button_start) {
			Intent intent = new Intent();
			intent.setClassName("jp.co.yahoo.sample.explicit",
					"jp.co.yahoo.sample.explicit.YConnectExplicitActivity");
			startActivity(intent);
    	}
	}
}
