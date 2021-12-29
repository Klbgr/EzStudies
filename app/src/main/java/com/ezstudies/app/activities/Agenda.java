package com.ezstudies.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;
import com.ezstudies.app.services.AlarmSetter;
import com.ezstudies.app.services.Login;
import com.ezstudies.app.services.RouteCalculator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Agenda extends FragmentActivity {
    private JavaScriptInterface jsi;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private ViewPager2 viewPager;
    private FragmentAdapter adapter;
    private broadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_layout);
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        viewPager = findViewById(R.id.agenda_viewpager);
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        viewPager.setAdapter(adapter);
        broadcastReceiver = new broadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("Agenda"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int page) {

            switch (page) {
                case 0 :
                    return new AgendaFragment(1);
                case 1 :
                    return new AgendaFragment(2);
                case 2 :
                    return new AgendaFragment(3);
                case 3 :
                    return new AgendaFragment(4);
                case 4 :
                    return new AgendaFragment(5);
                case 5 :
                    return new AgendaFragment(6);
                default:
                    return new AgendaFragment(1);
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }
    }

    public void import_celcat() {
        progressDialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.loading), true);
        String name = sharedPreferences.getString("name", null);
        String password = sharedPreferences.getString("password", null);
        String target = "AgendaLogin";
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("name", name);
        intent.putExtra("password", password);
        intent.putExtra("target", target);
        startService(intent);
        broadcastReceiver = new broadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(target));
    }

    public void parseCelcat(){
        Database database = new Database(this);
        database.clearAgenda();

        String source = jsi.getSource();
        Log.d("source", source);
        Document document = Jsoup.parse(source, "UTF-8");
        Elements days = document.getElementsByClass("fc-list-heading");
        for (Element e : days) { //days
            String date = e.attr("data-date");
            String dateElements[] = date.split("-");
            while(e.nextElementSibling() != null && e.nextElementSibling().className().equals("fc-list-item")){
                e = e.nextElementSibling();
                Element eHours = e.getElementsByClass("fc-list-item-time fc-widget-content").get(0);
                String hour = eHours.text();
                String startHour = hour.substring(0, hour.indexOf(" - "));
                String endHour = hour.substring(hour.indexOf(" - ")+3);
                String startHourSplit[] = startHour.split(":");
                String endHourSplit[] = endHour.split(":");
                String startingHour = startHourSplit[0];
                String startingMinute = startHourSplit[1];
                String endingHour = endHourSplit[0];
                String endingMinute = endHourSplit[1];
                Element eCourse = e.getElementsByClass("fc-list-item-title fc-widget-content").get(0);
                String course = eCourse.toString();
                course = course.substring(course.indexOf("</a>")+4, course.indexOf("</td>"));
                String courseInfo[] = course.split(" <br> ");
                String title = courseInfo[0] + " - " + courseInfo[1];
                String description = "";
                for(int i = 2 ; i<courseInfo.length ; i++){
                    description += courseInfo[i] + " / ";
                }
                description = description.substring(0, description.length()-3);
                String newDate = dateElements[2] + "/" + dateElements[1] + "/" + dateElements[0];
                database.addAgenda(newDate, title, startingHour + ":" + startingMinute, endingHour + ":" + endingMinute, description);
            }
        }
        Log.d("db", database.toStringAgenda());
        database.close();

        progressDialog.cancel();
        Toast.makeText(this, getString(R.string.celcat_success), Toast.LENGTH_SHORT).show();
        restart();
    }

    public void importICS(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/calendar");
        ActivityResultLauncher.launch(intent);
    }

    public void parseICS(String content){
        Database database = new Database(this);
        database.clearAgenda();
        try {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(content));
            String line;
            String date = null;
            String title = null;
            String startingAt = null;
            String endingAt = null;
            String description = null;
            String list[];
            while((line = bufferedReader.readLine()) != null) {
                list = line.split(":");
                switch (list[0]){
                    case "BEGIN": //start
                        if(list[1].equals("VEVENT")) {
                            date = null;
                            title = null;
                            startingAt = null;
                            endingAt = null;
                            description = null;
                        }
                        break;
                    case "DTSTART": //start time
                        startingAt = list[1].substring(9, 11) + ":" + list[1].substring(11, 13);
                        date = list[1].substring(6, 8) + "/" + list[1].substring(4, 6) + "/" + list[1].substring(0, 4);
                        break;
                    case "DTEND": //end time
                        endingAt = list[1].substring(9, 11) + ":" + list[1].substring(11, 13);
                        break;
                    case "DESCRIPTION": //description
                        description = list[1];
                        break;
                    case "SUMMARY": //title
                        title = list[1];
                        break;
                    case "END": //end
                        if(list[1].equals("VEVENT")) {
                            database.addAgenda(date, title, startingAt, endingAt, description);
                        }
                        break;
                }
            }
            bufferedReader.close();
            Log.d("db", database.toStringAgenda());
        } catch (IOException e) {
            e.printStackTrace();
        }
        database.close();

        Toast.makeText(this, getString(R.string.ics_success), Toast.LENGTH_SHORT).show();
        restart();
    }

    ActivityResultLauncher<Intent> ActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(result.getData().getData());
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            String content = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                content = content + line + "\n";
                            }
                            Log.d("uri", content);
                            bufferedReader.close();
                            inputStream.close();
                            parseICS(content);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void exportICS(View view) {
        Database database = new Database(this);
        String ics = database.toICS();
        database.close();
        try{
            Calendar now = Calendar.getInstance();
            String date = now.get(Calendar.DAY_OF_MONTH) + "" + (now.get(Calendar.MONTH)+1) + "" + now.get(Calendar.YEAR) + "" + now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + "" + now.get(Calendar.SECOND);
            String name = "celcat_" + date + ".ics";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name);
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(ics);
            fileWriter.close();
            Toast.makeText(this, getString(R.string.export_success) + name, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refresh(View view){
        int import_mode = sharedPreferences.getInt("import_mode", -1);
        switch (import_mode){
            case 0: //celcat
                import_celcat();
                break;
            case 1: //ics
                importICS();
                break;
            case -1:
                break;
        }
    }

    public void restart(){
        finish();
        startActivity(getIntent());
        Boolean alarm = sharedPreferences.getBoolean("alarm", false);
        if(alarm){
            setAlarms();
        }
    }

    public void setAlarms(){
        int mode = sharedPreferences.getInt("travel_mode", 0);
        Database database = new Database(this);
        ArrayList<String[]> firsts = database.getFirsts();
        database.close();
        if(mode != 2){
            String homeLat = sharedPreferences.getString("home_latitude", null);
            String homeLong = sharedPreferences.getString("home_longitude", null);
            String schoolLat = sharedPreferences.getString("school_latitude", null);
            String schoolLong = sharedPreferences.getString("school_longitude", null);

            Intent intent = new Intent(this, RouteCalculator.class);
            intent.putExtra("mode", mode);
            intent.putExtra("homeLat", homeLat);
            intent.putExtra("homeLong", homeLong);
            intent.putExtra("schoolLat", schoolLat);
            intent.putExtra("schoolLong", schoolLong);
            intent.putExtra("target", "Agenda");
            startService(intent);
        }
        else{
            int prep_time = sharedPreferences.getInt("prep_time", -1);
            int travel_time = sharedPreferences.getInt("travel_time", -1);
            for(String [] infos : firsts){
                int hour = Integer.parseInt(infos[1].split(":")[0]);
                int minute = Integer.parseInt(infos[1].split(":")[1]);
                int total = travel_time + prep_time;
                int diffHour = total/60;
                int diffMin = total - diffHour*60;
                hour -= diffHour;
                minute -= diffMin;
                if(minute<0){
                    minute += 60;
                    hour--;
                }
                infos[1] = hour + ":" + minute;
            }
            Intent intent = new Intent(this, AlarmSetter.class);
            intent.putExtra("list", firsts);
            startService(intent);
        }
    }

    private class broadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            if(target != null && target.equals("Agenda")){
                Database database = new Database(context);
                ArrayList<String[]> firsts = database.getFirsts();
                database.close();
                int duration = intent.getIntExtra("duration", -1);
                editor.putInt("duration", duration);
                editor.apply();
                int prep_time = sharedPreferences.getInt("prep_time", -1);
                for(String [] infos : firsts){
                    int hour = Integer.parseInt(infos[1].split(":")[0]);
                    int minute = Integer.parseInt(infos[1].split(":")[1]);
                    int total = duration/60 + prep_time;
                    int diffHour = total/60;
                    int diffMin = total - diffHour*60;
                    hour -= diffHour;
                    minute -= diffMin;
                    if(minute<0){
                        minute += 60;
                        hour--;
                    }
                    infos[1] = hour + ":" + minute;
                    Log.d("info", infos[1] + "");
                }
                Intent intent1 = new Intent(context, AlarmSetter.class);
                intent1.putExtra("list", firsts);
                startService(intent1);
            }
            else{
                String url = intent.getStringExtra("url");
                Boolean success = intent.getBooleanExtra("success", false);
                if(!success){
                    progressDialog.cancel();
                    Toast.makeText(context, getString(R.string.login_fail_network), Toast.LENGTH_SHORT).show();
                    return;
                }
                WebView webview = new WebView(context);
                webview.setWebViewClient(new myWebView(Agenda.this));
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setLoadWithOverviewMode(true);
                jsi = new JavaScriptInterface();
                webview.addJavascriptInterface(jsi, "HTMLOUT");
                HashMap<String, String> cookies = (HashMap<String, String>) intent.getSerializableExtra("cookies");
                for(Map.Entry<String, String> pair: cookies.entrySet()){
                    String cookie = pair.getKey() + "=" + pair.getValue() + "; path=/";
                    CookieManager.getInstance().setCookie(url, cookie);
                }
                webview.loadUrl(url);
            }
        }
    }

    private class JavaScriptInterface {
        private String source;
        @JavascriptInterface
        public void processHTML(String html){
            source = html;
        }

        public String getSource() {
            return source;
        }
    }

    private class myWebView extends WebViewClient {
        private Agenda agenda;
        private Boolean parsing = false;

        public myWebView(Agenda agenda){
            this.agenda = agenda;
        }
        public void onPageFinished(WebView view, String url) {
            Log.d("url", url);
            view.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            if (url.contains("fid0") && url.contains("listWeek") && !parsing){
                Log.d("parser", "parsing !!!");
                parsing = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        agenda.parseCelcat();
                    }
                }, 10000);
            }
        }
    }
}
