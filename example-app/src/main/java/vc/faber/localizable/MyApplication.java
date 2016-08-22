package vc.faber.localizable;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ivanbruel on 18/05/16.
 */
public class MyApplication extends Application {

    /*List<Locale> getLocales() {

        DisplayMetrics metrics = new DisplayMetrics();
        Resources r = getResources();
        Configuration c = r.getConfiguration();
        String[] loc = r.getAssets().getLocales();
        for (int i = 0; i < loc.length; i++) {
            Log.d("LOCALE", i + ": " + loc[i]);

            c.locale = new Locale(loc[i]);
            Resources res = new Resources(getAssets(), metrics, c);
            String s1 = res.getString(R.string.hello);
            c.locale = new Locale("");
            Resources res2 = new Resources(getAssets(), metrics, c);
            String s2 = res2.getString(R.string.hello);

            if(!s1.equals(s2)){
                Log.d("DIFFERENT LOCALE", i + ": "+ s1+" "+s2 +" "+ loc[i]);
            }
        }
        return new ArrayList();
    }

    List<String> getStringTokens() {
        InputStream input = getResources().
        Field[] fields = R.string.class.getDeclaredFields();
        List<String> stringTokens = new ArrayList();
        for (Field field : fields) {
            stringTokens.add(field.getName());
            Log.d("TOKEN", field.getName());
        }
        return stringTokens;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        //List<String> tokens = getStringTokens();
        //getLocales();


        /*getStringsForLocale();
        Field[] fields = R.string.class.getDeclaredFields(); // or Field[] fields = R.string.class.getFields();
        String str = "";
        for (int  i =0; i < fields.length; i++) {
            int resId = getResources().getIdentifier(fields[i].getName(), "string", getPackageName());
            str += fields[i].getName() + " = ";
            if (resId != 0) {
                str += getResources().getString(resId);
            }
            str += "\n";
        }*/


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
