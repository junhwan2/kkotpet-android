package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;

/**
 * @brief Facebook sdk 연동 클래스
 */

public class FacebookSDKAdapter extends SNSBaseAdapter {
    private CallbackManager callbackManager = null;

    public FacebookSDKAdapter(Context context, SNSInstance.SNSCallback snsCallback) {
        super(context, snsCallback);
    }

    @Override
    public void loginSNS() {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AppLog.Debug("facebook token : " + loginResult.getAccessToken().getToken());
                AppLog.Debug("facebook userId : " + loginResult.getAccessToken().getUserId());

                reqFacebookUserInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                AppLog.Debug("facebook login cancel");
            }

            @Override
            public void onError(FacebookException error) {
                AppLog.Debug("facebook login error");

                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }

                if (snsCallback != null) {
                    snsCallback.SNSError(SNSInstance.SNS_TYPE_FACEBOOK, error.getLocalizedMessage());
                }
            }

        });

        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @brief Facebook 로그인 요청
     */
    private void reqFacebookUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //페이스북 로그인 결과 값
                        try {
                            String id = getString(object, "id");
                            String email = getString(object, "email");
                            String name = getString(object, "name");
                            String gender = getString(object, "gender");
                            String birthday = getString(object, "birthday");
                            String link = getString(object, "link");
                            String locale = getString(object, "locale");
                            String timezone = getString(object, "timezone");

                            String profilePicUrl = "";
                            if (object.has("picture"))
                                profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");


                            SNSUserInfoDTO userInfoDTO = new SNSUserInfoDTO();
                            userInfoDTO.setId(id);
                            userInfoDTO.setEmail(email);
                            userInfoDTO.setName(name);
                            userInfoDTO.setGender(gender);
                            userInfoDTO.setBirth(birthday);
                            userInfoDTO.setSnslink(link);
                            userInfoDTO.setLocale(locale);
                            userInfoDTO.setTimezone(timezone);
                            userInfoDTO.setSnstype(SNSInstance.SNS_TYPE_FACEBOOK);
                            userInfoDTO.setAccessToken(AccessToken.getCurrentAccessToken().getToken()); // Access Token

                            userInfoDTO.setImgurl(profilePicUrl);

                            if (snsCallback != null) {
                                snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_FACEBOOK, userInfoDTO);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (snsCallback != null) {
                                snsCallback.SNSError(SNSInstance.SNS_TYPE_FACEBOOK, e.getLocalizedMessage());
                            }
                        }
                    }
                });

        Bundle param = new Bundle();
        param.putString("fields", "id, email, name, gender, birthday, link, locale, timezone, picture.width(150).height(150)");
        request.setParameters(param);
        request.executeAsync();
    }

    /**
     * @param object JSON 객체
     * @param key    key값
     * @return Valeu 값 반환
     * @brief JSON Value 값
     */
    private String getString(JSONObject object, String key) {
        String sVal = "";
        if (object != null && object.has(key)) {
            try {
                sVal = object.getString(key);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AppLog.Debug("sns key : " + key + "  sVal : " + sVal);

        return sVal;
    }

}

