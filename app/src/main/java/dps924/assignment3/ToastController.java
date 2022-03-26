package dps924.assignment3;

import android.content.Context;
import android.widget.Toast;

public class ToastController {
    private static Toast TheToast;

    public static void showToast(Context context, String message) {
        if (TheToast != null)
            TheToast.cancel();
        TheToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        TheToast.show();
    }
}