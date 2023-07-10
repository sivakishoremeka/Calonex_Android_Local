package mp.app.calonex.common.utility;

import android.util.Patterns;
import android.widget.TextView;

import java.util.ArrayList;

public class ValidationForm {

    ArrayList<FormField> formFields;

    private ValidationForm() {
        formFields = new ArrayList<FormField>();
    }

    public static ValidationForm create() {
        return new ValidationForm();
    }

    public void add(FormField formField) {
        formFields.add(formField);
    }

    public FormField getFormField(TextView textView) {
        for (FormField formField : formFields) {
            if (formField.getTextView() == textView) {
                return formField;
            }
        }
        return null;
    }

    public boolean validate() {
        boolean validate = true;
        for (FormField formField : formFields) {
            String value = formField.getTextView().getText().toString();
            // if optional
            if (formField.isOptional() && value.equals("")) {
                continue;
            }
            boolean v = false;
            switch (formField.getType()) {
                case WEBLINK:
                    v = Patterns.WEB_URL.matcher(formField.getTextView().getText()
                            .toString())
                            .matches();
                    break;
                case EMAIL:
                    v = Patterns.EMAIL_ADDRESS.matcher(formField.getTextView().getText()
                            .toString())
                            .matches();
                    break;
                case PASSWORD:
                    v = !formField.getTextView().getText().toString().equals("");
                    boolean p1 = false, p2 = false;
                    if (formField.getMinLength() != -1) {
                        p1 = formField.getTextView().getText().toString().length() >= formField.getMinLength();
                        formField.setErrorMessage("Even Parties Need To Be Secure\n" +
                                "Password should be 6 to 20 digits");
                    } else {
                        p1 = true;
                    }
                    if (formField.getMaxLength() != -1) {
                        p2 = formField.getTextView().getText().toString().length() <= formField.getMaxLength();
                        formField.setErrorMessage("Even Parties Need To Be Secure\n" +
                                "Password should be 6 to 20 digits");
                    } else {
                        p2 = true;
                    }
                    v = v && p1 && p2;
                    break;
                case DATE:
                    if (value.contains("MM"))
                    break;
                case SIMPLE:
                    v = !formField.getTextView().getText().toString().equals("");
                    boolean v1 = false, v2 = false;
                    if (formField.getMinLength() != -1) {
                        v1 = formField.getTextView().getText().toString().length() >= formField.getMinLength();
                        formField.setErrorMessage("Minimum of " + formField.getMinLength() + " characters allowed");
                    } else {
                        v1 = true;
                    }
                    if (formField.getMaxLength() != -1) {
                        v2 = formField.getTextView().getText().toString().length() <= formField.getMaxLength();
                        formField.setErrorMessage("Maximum of " + formField.getMaxLength() + " characters allowed");
                    } else {
                        v2 = true;
                    }
                    v = v && v1 && v2;
                    break;
            }
            if (v) {
                formField.getTextView().setError(null);
            } else {
                validate = false;
                formField.getTextView().setError(formField.getErrorMessage());
            }
        }
        return validate;
    }
}
