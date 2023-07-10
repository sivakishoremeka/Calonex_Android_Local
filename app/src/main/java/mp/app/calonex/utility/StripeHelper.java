package mp.app.calonex.utility;


import com.stripe.android.exception.StripeException;

public class StripeHelper {
    public static String piiToken(String ssnNo) throws StripeException {
      /* Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";

        Map<String, Object> pii = new HashMap<>();
        pii.put("id_number", ssnNo);
        Map<String, Object> params = new HashMap<>();
        params.put("pii", pii);

        Token token = Token.create(params);
        return token.toString();*/
     return null;
    }
}
