package mp.app.calonex.common.utility;

import android.widget.TextView;

/**
 * The class represents a form field and helps in identifying errors on the user inputs
 * Created by Gaurav on 20/8/20.
 */
public class FormField {

    public enum TYPE {
        WEBLINK, EMAIL, PASSWORD, SIMPLE , DATE
    }

    TextView textView;
    TYPE type;
    String errorMessage;
    boolean watcher;
    boolean optional;
    int minLength = -1;
    int maxLength = -1;

    public boolean isWatcher() {
        return watcher;
    }

    public FormField setWatcher(boolean watcher) {
        this.watcher = watcher;
        return this;
    }

    public boolean isOptional() {
        return optional;
    }

    public FormField setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public FormField setMinLength(int minLength) {
        this.minLength = minLength;
        return this;
    }

    public FormField setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public FormField setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getErrorMessage() {
        if (errorMessage == null) {
            switch (type) {
                case WEBLINK:
                    return "Incorrect Web Address";
                case EMAIL:
                    if (getTextView().length() == 0)
                        return "Enter the Email Id";
                    return "Incorrect Email Address";
                case PASSWORD:
                    return "Field can't be left blank";
                case SIMPLE:
                    return "Field can't be left blank";
                case DATE:
                    return  "Field can't be left blank";
            }
        }
        return errorMessage;
    }

    public static FormField create(TextView textView, TYPE type) {
        return new FormField(textView, type);
    }

    private FormField(TextView textView, TYPE type) {
        this.textView = textView;
        this.type = type;
        minLength = -1;
    }
}
