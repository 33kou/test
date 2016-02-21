package jp.co.yahoo.sample.explicit;

import jp.co.yahoo.yconnect.YConnectExplicit;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Explicit(Authorization Code Flow) Sample Activity
 *
 * @author Copyright (C) 2012 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class YConnectExplicitActivity extends Activity {

	private final static String TAG = YConnectExplicitActivity.class.getSimpleName();

	// 繧｢繝励Μ繧ｱ繝ｼ繧ｷ繝ｧ繝ｳID
	public final static String clientId = "";

	// 繧ｳ繝ｼ繝ｫ繝舌ャ繧ｯURI
	// (繧｢繝励Μ繧ｱ繝ｼ繧ｷ繝ｧ繝ｳID逋ｺ陦梧凾縺ｫ逋ｻ骭ｲ縺励◆URI)
	public final static String customUriScheme = "dj0zaiZpPTFxdnY4cVAxZTJZTCZzPWNvbnN1bWVyc2VjcmV0Jng9MzY-";

	public final static String YCONNECT_PREFERENCE_NAME = "yconnect";

	@Override
    public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.explicit);
		SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);

		// YConnect繧､繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ蜿門ｾ�
		YConnectExplicit yconnect = YConnectExplicit.getInstance();

		// 繝ｭ繧ｰ繝ｬ繝吶Ν險ｭ螳夲ｼ亥ｿ�隕√↓蠢懊§縺ｦ繝ｬ繝吶Ν繧定ｨｭ螳壹＠縺ｦ縺上□縺輔＞�ｼ�
		// YConnectLogger.setLogLevel(YConnectLogger.DEBUG);

		Intent intent = getIntent();

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {

			/**********************************************************
			      Parse the Callback URI and Save the Access Token.
			 **********************************************************/

			try {

				Log.i(TAG, "Get callback uri and parse it.");

				// state縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ
				String state = sharedPreferences.getString("state", null);

				// 繧ｳ繝ｼ繝ｫ繝舌ャ繧ｯURL縺九ｉ蜷�繝代Λ繝｡繝ｼ繧ｿ繝ｼ繧呈歓蜃ｺ
				Uri uri = intent.getData();
				yconnect.parseAuthorizationResponse(uri, customUriScheme, state);
				// 隱榊庄繧ｳ繝ｼ繝峨ｒ蜿門ｾ�
				String code = yconnect.getAuthorizationCode();

				TextView codeTV = (TextView) findViewById(R.id.code);
				codeTV.setText(code);

				// 蛻･繧ｹ繝ｬ繝�繝�(AsynckTask)縺ｧToken縲，heckToken縲ゞserInfo繧ｨ繝ｳ繝峨�昴う繝ｳ繝医↓繝ｪ繧ｯ繧ｨ繧ｹ繝�
				YConnectExplicitAsyncTask asyncTask = new YConnectExplicitAsyncTask(this, code, customUriScheme);
				asyncTask.execute("Request Access Token.");

			} catch (AuthorizationException e) {
				Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			/****************************************************************
			     Request Authorization Endpoint for getting Access Token.
			 ****************************************************************/

			Log.i(TAG, "Request authorization.");

			// 蜷�繝代Λ繝｡繝ｼ繧ｿ繝ｼ蛻晄悄蛹�
			// 繝ｪ繧ｯ繧ｨ繧ｹ繝医→繧ｳ繝ｼ繝ｫ繝舌ャ繧ｯ髢薙�ｮ讀懆ｨｼ逕ｨ縺ｮ繝ｩ繝ｳ繝�繝�縺ｪ譁�蟄怜�励ｒ謖�螳壹＠縺ｦ縺上□縺輔＞
			String state = "44GC44GC54Sh5oOF";
			// 繝ｪ繝励Ξ繧､繧｢繧ｿ繝�繧ｯ蟇ｾ遲悶�ｮ繝ｩ繝ｳ繝�繝�縺ｪ譁�蟄怜�励ｒ謖�螳壹＠縺ｦ縺上□縺輔＞
			String nonce = "U0FNTCBpcyBEZWFkLg==";
			String display = OIDCDisplay.SMART_PHONE;
			String[] prompt = { OIDCPrompt.DEFAULT };
			String[] scope = { OIDCScope.OPENID, OIDCScope.PROFILE,
					OIDCScope.EMAIL, OIDCScope.ADDRESS };

			// state縲］once繧剃ｿ晏ｭ�
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("state", state);
			editor.putString("nonce", nonce);
			editor.commit();

			// 蜷�繝代Λ繝｡繝ｼ繧ｿ繝ｼ繧定ｨｭ螳�
			yconnect.init(clientId, customUriScheme, state, display, prompt, scope, nonce);
			// Authorization繧ｨ繝ｳ繝峨�昴う繝ｳ繝医↓繝ｪ繧ｯ繧ｨ繧ｹ繝�
			// (繝悶Λ繧ｦ繧ｶ繝ｼ繧定ｵｷ蜍輔＠縺ｦ蜷梧э逕ｻ髱｢繧定｡ｨ遉ｺ)
			yconnect.requestAuthorization(this);

		}

	}

}