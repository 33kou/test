package jp.co.yahoo.sample.explicit;

import jp.co.yahoo.yconnect.YConnectExplicit;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oauth2.TokenException;
import jp.co.yahoo.yconnect.core.oidc.CheckIdException;
import jp.co.yahoo.yconnect.core.oidc.IdTokenObject;
import jp.co.yahoo.yconnect.core.oidc.UserInfoObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Explicit(Authorization Code Flow) Sample Class
 *
 * @author Copyright (C) 2012 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class YConnectExplicitAsyncTask extends AsyncTask<String, Integer, Long> {

	private final static String TAG = YConnectExplicitAsyncTask.class.getSimpleName();

	private Activity activity;
	private Handler handler;

	private String clientId;
	private String code;
	private String redirectUri;

	public YConnectExplicitAsyncTask(Activity activity, String code, String redirectUri) {
		this.activity = activity;
		this.clientId = YConnectExplicitActivity.clientId;
		this.code = code;
		this.redirectUri = redirectUri;
		this.handler = new Handler();
	}

	@Override
	protected Long doInBackground(String... params) {

		Log.d(TAG, params[0]);

		SharedPreferences sharedPreferences = activity.getSharedPreferences(
				YConnectExplicitActivity.YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // YConnectインスタンス取得
		YConnectExplicit yconnect = YConnectExplicit.getInstance();

		try {

        	/************************************************
   	    	     Request Access Token and Refresh Token.
   	         ************************************************/

			Log.i(TAG, "Request Access Token and Refresh Token.");

			// Tokenエンドポイントにリクエスト
			yconnect.requestToken(code, redirectUri, clientId);
			// アクセストークン、リフレッシュトークン、IDトークンを取得
			final String accessTokenString = yconnect.getAccessToken();
			final long expiration = yconnect.getAccessTokenExpiration();
			final String refreshToken = yconnect.getRefreshToken();
			final String idTokenString = yconnect.getIdToken();

			// アクセストークン、リフレッシュトークンを保存
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("access_token", accessTokenString);
			editor.putString("refresh_token", refreshToken);
			editor.commit();

			handler.post(new Runnable() {
				public void run() {
					TextView accessToeknTV = (TextView) activity.findViewById(R.id.access_token);
					TextView expirationTV = (TextView) activity.findViewById(R.id.expiration);
					TextView refreshTokenTV = (TextView) activity.findViewById(R.id.refresh_token);
					TextView idTokenTV = (TextView) activity.findViewById(R.id.id_token);
					accessToeknTV.setText(accessTokenString);
					expirationTV.setText(Long.toString(expiration));
					refreshTokenTV.setText(refreshToken);
					idTokenTV.setText(idTokenString);
				}
			});

        	/***********************
	    	     Check ID Token.
	         ***********************/

	    	Log.i(TAG, "Request ChechToken.");

	    	// nonceの読み込み
	    	String nonce = sharedPreferences.getString("nonce", null);

            // CheckTokenエンドポイントにリクエスト
			yconnect.requestCheckToken(idTokenString, nonce, clientId);
			// 復号化されたIDトークン情報を取得
			final IdTokenObject idtokenObject = yconnect.getIdTokenObject();

			handler.post(new Runnable() {
				public void run() {
					TextView checkTokenTV = (TextView) activity.findViewById(R.id.check_token);
					checkTokenTV.setText(idtokenObject.toString());
				}
			});

			/*************************
			     Request UserInfo.
			 *************************/

			Log.i(TAG, "Request UserInfo.");

			// アクセストークンの読み込み
			String storedAccessToken = sharedPreferences.getString("access_token", null);

			// UserInfoエンドポイントにリクエスト
			yconnect.requestUserInfo(storedAccessToken);
			// UserInfo情報を取得
			final UserInfoObject userInfoObject = yconnect.getUserInfoObject();

			handler.post(new Runnable() {
				public void run() {
					TextView userInfoTV = (TextView) activity.findViewById(R.id.user_info);
					userInfoTV.setText(userInfoObject.toString());
				}
			});

		} catch (ApiClientException e) {

			// エラーレスポンスが"Invalid_Token"であるかチェック
			if(e.isInvalidToken()) {

				/*****************************
	        	     Refresh Access Token.
	        	 *****************************/

				Log.i(TAG, "Refresh Access Token.");

				try {

					// リフレッシュトークンの読み込み
					String refreshToken = sharedPreferences.getString("refresh_token", null);

					// Tokenエンドポイントにリクエストしてアクセストークンを更新
					yconnect.refreshToken(refreshToken, clientId);
					String accessTokenString = yconnect.getAccessToken();

					// アクセストークン、リフレッシュトークンを保存
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("access_token", accessTokenString);
					editor.commit();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
			e.printStackTrace();
		} catch (TokenException e) {
			Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
			e.printStackTrace();
		} catch (CheckIdException e) {
			Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "error=" + e.getMessage());
			e.printStackTrace();
		}

		return 1L;
	}

}
