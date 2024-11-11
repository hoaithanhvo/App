package com.example.nidecsnipeit.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.Context;
import android.util.Log;

import com.example.nidecsnipeit.activity.BaseActivity;
import com.example.nidecsnipeit.activity.LoginActivity;
import com.example.nidecsnipeit.activity.MenuActivity;
import com.example.nidecsnipeit.activity.ProductDeliveryActivity;
import com.example.nidecsnipeit.activity.SettingsActivity;
import com.example.nidecsnipeit.network.NetworkManager;

import java.util.Locale;

public class LocaleHelper extends BaseActivity {
    private SharedPreferences preferences;
    private String flagRes="";
    private static LocaleHelper instance = null;
    private Context context;
    public static synchronized LocaleHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocaleHelper(context);
        }
        return instance;
    }

    private LocaleHelper(Context context){
        this.context= context.getApplicationContext();
        preferences = context.getSharedPreferences("AppPrefs",context.MODE_PRIVATE);
    }

    public static synchronized LocaleHelper getInstance() {
        if (null == instance) {
            throw new IllegalStateException(LocaleHelper.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }
    public void applyLanguage(Context context) {
        final String language[] = {"English","VietNam","Japan"};
        AlertDialog.Builder  mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Change Language");

        int selectedLanguageIndex = preferences.getInt("SelectedLanguageIndex", -1);
        mBuilder.setSingleChoiceItems(language, selectedLanguageIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String languageCode ="";
                if(which==0){
                    languageCode ="";
                    flagRes = "img_flag_uk";
                }else if(which==1){
                    languageCode ="vi";
                    flagRes = "img_flag_vi";
                }else if(which==2){
                    languageCode ="ja";
                    flagRes = "img_flag_ja";
                }
                lan(languageCode);
                saveLanguageToPreferences(languageCode,which,flagRes);
                restartApp(context);
            }
        });
        mBuilder.create();
        mBuilder.show();
    }

    public void lan(String s) {
        if (context != null) {
            Locale locale = new Locale(s);
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            try {
                context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("LocaleHelper", "Context is null, cannot update language configuration.");
        }
    }

    private void saveLanguageToPreferences(String languageCode,int index,String flagImage) {
        preferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LanguagePrefs", languageCode);
        editor.putInt("SelectedLanguageIndex", index);
        editor.putString("SelectFlagImage",flagImage);
        editor.apply();
    }
    private void restartApp(Context context) {
        if (context != null && context instanceof Activity) {
            if (context instanceof LoginActivity) {
                ((LoginActivity) context).recreate();
            }
            else if(context instanceof SettingsActivity){
                Intent intentChangeLanguage = new Intent(context.getApplicationContext(),MenuActivity.class);
                intentChangeLanguage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intentChangeLanguage);
                ((Activity) context).finish();
            }
        } else {
            Log.e("LocaleHelper", "Context is either null or not an instance of Activity.");
        }
    }
}
