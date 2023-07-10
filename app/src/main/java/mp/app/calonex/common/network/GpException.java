package mp.app.calonex.common.network;

/**
 * The class is a custom exception wrapping up the exception within
 * This is created to be free from the VolleyError class, if required to be changed in future
 * Created by GoParties on 01/12/15.
 */
public class GpException extends Error {

    public static final String INVALID_METHOD = "404";
    public static final String PARAMS_MISSING = "101";
    public static final String INVALID_TOKEN = "403";
    public static final String CUSTOM_ERROR = "104";
    public static final String NO_NETWORK = "-1";

    public static final String INVALID_CRED = "105";
    public static final String NOT_VERIFIED = "106";
    public static final String USER_EXISTS = "107";
    public static final String USER_BANNED = "110";

    public static final String NA = "100";

    String code;

    public GpException(String code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public boolean isNetworkIssue() {
        return getCode().equals(NO_NETWORK);
    }

    public boolean isInValidCred() {
        return getCode().equals(INVALID_CRED);
    }

    public boolean isNotVerified() {
        return getCode().equals(NOT_VERIFIED);
    }

    public boolean isUserExists() {
        return getCode().equals(USER_EXISTS);
    }

    public boolean isUpdateRequired() {
        return getCode().equals(INVALID_TOKEN)
                || getCode().equals(INVALID_METHOD)
                || getCode().equals(PARAMS_MISSING);
    }

    public boolean isCustom() {
        return getCode().equals(CUSTOM_ERROR);
    }

    @Override
    public String toString() {
        return "GpException{" +
                "code='" + code + '\'' +
                "message='" + getMessage() + '\'' +
                '}';
    }
}
