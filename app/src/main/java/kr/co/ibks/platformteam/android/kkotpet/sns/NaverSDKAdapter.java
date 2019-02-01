package kr.co.ibks.platformteam.android.kkotpet.sns;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kr.co.ibks.platformteam.android.kkotpet.R;
import kr.co.ibks.platformteam.android.kkotpet.util.AppLog;

/**
 * @brief Naver sdk 연동 클래스
 */

public class NaverSDKAdapter extends SNSBaseAdapter {
    private static OAuthLogin mOAuthLoginInstance = null;

    public NaverSDKAdapter(Context context, SNSInstance.SNSCallback snsCallback) {
        super(context, snsCallback);
    }

    @Override
    public void loginSNS() {
        if (mOAuthLoginInstance == null) {
            mOAuthLoginInstance = OAuthLogin.getInstance();

            mOAuthLoginInstance.init(
                    context,
                    context.getString(R.string.naver_client_id),
                    context.getString(R.string.naver_client_secret),
                    context.getString(R.string.app_name));
        }

        new DeleteTokenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        mOAuthLoginInstance.startOauthLoginActivity((Activity) context, mOAuthLoginHandler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

//    /**
//     * @brief 결과값 파싱
//     */
//    private void responseNaverProfile(String sData) {
//        try {
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            XmlPullParser parser = factory.newPullParser();
//
//            InputStream is = new ByteArrayInputStream(sData.getBytes("UTF-8"));
//            parser.setInput(new InputStreamReader(is, "UTF-8"));
//
//            int eventType = parser.getEventType();
//
//            String email = "";
//            String nickName = "";
//            String profileImg = "";
//            String age = "";
//            String gender = "";
//            String id = "";
//            String name = "";
//            String birth = "";
//
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        String startTag = parser.getName();
//                        if (startTag.equals("email")) {
//                            email = parser.nextText();
//                        } else if (startTag.equals("nickname")) {
//                            nickName = parser.nextText();
//                        } else if (startTag.equals("profile_image")) {
//                            profileImg = parser.nextText();
//                        } else if (startTag.equals("age")) {
//                            age = parser.nextText();
//                        } else if (startTag.equals("gender")) {
//                            gender = parser.nextText();
//                        } else if (startTag.equals("id")) {
//                            id = parser.nextText();
//                        } else if (startTag.equals("name")) {
//                            name = parser.nextText();
//                        } else if (startTag.equals("birthday")) {
//                            birth = parser.nextText();
//                        }
//                        break;
//                    case XmlPullParser.END_TAG:
//                        String endTag = parser.getName();
//                        break;
//                }
//
//                eventType = parser.next();
//            }
//
//
//            SNSUserInfoDTO userInfoDTO = new SNSUserInfoDTO();
//            userInfoDTO.setName(nickName);
//            userInfoDTO.setId(id);
//            userInfoDTO.setImgurl(profileImg);
//            userInfoDTO.setGender(gender);
//            userInfoDTO.setEmail(email);
//            //userInfoDTO.setAge(age);
//            userInfoDTO.setBirth(birth);
//            userInfoDTO.setSnstype(SNSInstance.SNS_TYPE_NAVER);
//
//            String token = mOAuthLoginInstance.getAccessToken(context);
//            String encToken = null;
//            try {
//                encToken = URLEncoder.encode(token, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            userInfoDTO.setAccessToken(encToken); // Access Token
//
//            if (snsCallback != null) {
//                snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_NAVER, userInfoDTO);
//            }
//
//            DeleteTokenTask deleteTokenTask = new DeleteTokenTask();
//            deleteTokenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            if (snsCallback != null) {
//                snsCallback.SNSError(SNSInstance.SNS_TYPE_NAVER, e.getLocalizedMessage());
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            if (snsCallback != null) {
//                snsCallback.SNSError(SNSInstance.SNS_TYPE_NAVER, e.getLocalizedMessage());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            if (snsCallback != null) {
//                snsCallback.SNSError(SNSInstance.SNS_TYPE_NAVER, e.getLocalizedMessage());
//            }
//        }
//    }

    /**
     * 로그인 성공 처리
     */
    private void processLoginSuccess() {
        SNSUserInfoDTO userInfoDTO = new SNSUserInfoDTO();
        userInfoDTO.setSnstype(SNSInstance.SNS_TYPE_NAVER);

        String token = mOAuthLoginInstance.getAccessToken(context);
        String encToken = null;
        try {
            encToken = URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        userInfoDTO.setAccessToken(encToken); // Access Token

        if (snsCallback != null) {
            snsCallback.SNSLoginSuccess(SNSInstance.SNS_TYPE_NAVER, userInfoDTO);
        }
    }

    /**
     * 로그인 실패 처리
     *
     * @param errorMsg
     */
    private void processLoginFail(String errorMsg) {
        if (snsCallback != null) {
            snsCallback.SNSError(SNSInstance.SNS_TYPE_NAVER, errorMsg);
        }
    }

    /**
     * @brief startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    @SuppressLint("HandlerLeak")
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                AppLog.Debug("Naver AccessToken:" + mOAuthLoginInstance.getAccessToken(context));
                AppLog.Debug("Naver RefreshToken:" + mOAuthLoginInstance.getRefreshToken(context));
                AppLog.Debug("Naver ExpiresAt:" + mOAuthLoginInstance.getExpiresAt(context));
                AppLog.Debug("Naver TokenType:" + mOAuthLoginInstance.getTokenType(context));
                AppLog.Debug("Naver State:" + mOAuthLoginInstance.getState(context).toString());

                new RequestApiTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                processLoginSuccess();
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(context).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(context);
                AppLog.Debug("Naver errorCode:" + errorCode + ", errorDesc:" + errorDesc);

                processLoginFail("Naver errorCode:" + errorCode + ", errorDesc:" + errorDesc);
            }
        }
    };

    /**
     * @brief 토큰 삭제
     */
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(context);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                AppLog.Debug("errorCode:" + mOAuthLoginInstance.getLastErrorCode(context));
                AppLog.Debug("errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(context));
            }

            mOAuthLoginInstance.startOauthLoginActivity((Activity) context, mOAuthLoginHandler);

            return null;
        }

        protected void onPostExecute(Void v) {
            AppLog.Debug("Naver AccessToken:" + mOAuthLoginInstance.getAccessToken(context));
            AppLog.Debug("Naver RefreshToken:" + mOAuthLoginInstance.getRefreshToken(context));
            AppLog.Debug("Naver ExpiresAt:" + mOAuthLoginInstance.getExpiresAt(context));
            AppLog.Debug("Naver TokenType:" + mOAuthLoginInstance.getTokenType(context));
            AppLog.Debug("Naver State:" + mOAuthLoginInstance.getState(context));

        }
    }

    /**
     * @brief API 호출
     */
    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(context);
            return mOAuthLoginInstance.requestApi(context, at, url);
        }

        protected void onPostExecute(String content) {
            AppLog.Debug("Naver API Result:" + content);

//            responseNaverProfile(content);

            processLoginSuccess();
        }
    }

//    /**
//     * @brief 토큰 다시 받기
//     */
//    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... params) {
//            return mOAuthLoginInstance.refreshAccessToken(context);
//        }
//
//        protected void onPostExecute(String res) {
//            AppLog.Debug("Naver AccessToken:" + mOAuthLoginInstance.getAccessToken(context));
//            AppLog.Debug("Naver RefreshToken:" + mOAuthLoginInstance.getRefreshToken(context));
//            AppLog.Debug("Naver ExpiresAt:" + mOAuthLoginInstance.getExpiresAt(context));
//            AppLog.Debug("Naver TokenType:" + mOAuthLoginInstance.getTokenType(context));
//            AppLog.Debug("Naver State:" + mOAuthLoginInstance.getState(context));
//        }
//    }
}
