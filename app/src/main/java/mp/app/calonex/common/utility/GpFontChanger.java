package mp.app.calonex.common.utility;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gaurav on 20/8/20
 */
public class GpFontChanger {

    public static final String JENNESUE = "jennasue.otf";
    public static final String MISO = "miso_regular.otf";
    public static final String ROBOTO = "roboto_regular.otf";

    synchronized public static void setFont(View view, String regular, String bold) {
        try {
            if (view == null)
                return;
            Typeface tf_regular = Typeface.createFromAsset(view.getContext().getAssets(), regular);
            Typeface tf_bold = Typeface.createFromAsset(view.getContext().getAssets(), bold);
            doChange(view, tf_regular, tf_bold);
        } catch (Exception e) {
            Log.e("crash", "");
        }
    }

    synchronized private static void doChange(ViewGroup viewGroup, Typeface regular, Typeface bold) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                doChange((ViewGroup) child, regular, bold);
            } else if (child instanceof TextView) {
                doChange(child, regular, bold);
            }
        }
    }

    synchronized private static void doChange(View child, Typeface regular, Typeface bold) {
        if (child instanceof TextView) {
            try {
                if (((TextView) child).getTypeface().getStyle() == Typeface.BOLD)
                    ((TextView) child).setTypeface(bold, Typeface.BOLD);
                else
                    ((TextView) child).setTypeface(regular);
            } catch (Exception e) {
                ((TextView) child).setTypeface(regular);
            }
        } else if (child instanceof ViewGroup) {
            doChange((ViewGroup) child, regular, bold);
        }
    }
}
