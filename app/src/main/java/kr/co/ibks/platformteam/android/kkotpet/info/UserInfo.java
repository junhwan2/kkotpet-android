package kr.co.ibks.platformteam.android.kkotpet.info;

import kr.co.ibks.platformteam.android.kkotpet.sns.SNSInstance;

public class UserInfo {

    /**
     * @param nType 타입코드
     * @return 타입명
     * @brief SNS 타입코드에 대응하는 타입명
     */
    public static String getSNSType(int nType) {
        String sSns = "";
        switch (nType) {
            case SNSInstance.SNS_TYPE_FACEBOOK:
                sSns = "facebook";
                break;
            case SNSInstance.SNS_TYPE_KAKAO:
                sSns = "kakao";
                break;
            case SNSInstance.SNS_TYPE_NAVER:
                sSns = "naver";
                break;
            case SNSInstance.SNS_TYPE_PAYCO:
                sSns = "payco";
                break;
//            case SNSInstance.SNS_TYPE_GOOGLEP:
//                sSns = "google";
//                break;
//            case SNSInstance.SNS_TYPE_TWITTER:
//                sSns = "twiter";
//                break;
//            case SNSInstance.SNS_TYPE_FB_ACCOUNT:
//                sSns = "accountKit";
//                break;
        }

        return sSns;
    }
}
