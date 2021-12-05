package com.ezstudies.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.agenda.Course;
import com.ezstudies.app.agenda.Date;
import com.ezstudies.app.agenda.Day;
import com.ezstudies.app.agenda.Hours;
import com.ezstudies.app.agenda.Week;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CelcatParser extends AppCompatActivity {
    private WebView webview;
    private static JavaScriptInterface jsi;
    private static Week week;
    private static String ics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = Welcome.url;
        webview = new WebView(this);
        webview.setWebViewClient(new Webview());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        jsi = new JavaScriptInterface();
        webview.addJavascriptInterface(jsi, "HTMLOUT");
        HashMap<String, String> cookies = (HashMap<String, String>) Welcome.cookies;
        for(Map.Entry<String, String> pair: cookies.entrySet()){
            String cookie = pair.getKey() + "=" + pair.getValue() + "; path=/";
            CookieManager.getInstance().setCookie(url, cookie);
        }
        webview.loadUrl(url);
    }

    public static void parse() {
        String source = jsi.getSource();
        Log.d("source", source);
        ArrayList<String> daysList = new ArrayList();
        Week week = new Week();
        Document document = Jsoup.parse(source, "UTF-8");
        Elements days = document.getElementsByClass("fc-list-heading");
        for (Element e : days) { //days
            String date = e.attr("data-date");
            String dateElements[] = date.split("-");
            Log.d("jour", dateElements[2].toString());
            Day day = new Day(new Date(Integer.parseInt(dateElements[2]), Integer.parseInt(dateElements[1]), Integer.parseInt(dateElements[0])));
            while(e.nextElementSibling() != null && e.nextElementSibling().className().equals("fc-list-item")){
                e = e.nextElementSibling();
                Element ehorraires = e.getElementsByClass("fc-list-item-time fc-widget-content").get(0);
                String horraire = ehorraires.text();
                String horraireDebut = horraire.substring(0, horraire.indexOf(" - "));
                Log.d("horraireDebut", horraireDebut);
                String horraireFin = horraire.substring(horraire.indexOf(" - ")+3);
                Log.d("horraireFin", horraireFin);
                String horrairesDebutTab[] = horraireDebut.split(":");
                String horrairesFinTab[] = horraireFin.split(":");
                int startHour = Integer.parseInt(horrairesDebutTab[0]);
                int startMinute = Integer.parseInt(horrairesDebutTab[1]);
                int endHour = Integer.parseInt(horrairesFinTab[0]);
                int endMinute = Integer.parseInt(horrairesFinTab[1]);
                Element ecours = e.getElementsByClass("fc-list-item-title fc-widget-content").get(0);
                String cours = ecours.toString();
                cours = cours.substring(cours.indexOf("</a>")+4, cours.indexOf("</td>"));
                Log.d("cours", cours);
                String infoCours[] = cours.split(" <br> ");
                Course c = null;
                if(infoCours.length == 4){
                    String title = infoCours[0] + " - " + infoCours[1];
                    String description  = infoCours[2] + " | " + infoCours[3];
                    c = new Course(new Hours(startHour, startMinute, endHour, endMinute), title, description);
                }else {
                    if(infoCours.length != 0){
                        String title = infoCours[0];
                        String description = null;
                        for(String info : infoCours){
                            description += info + " - ";
                        }
                        c = new Course(new Hours(startHour, startMinute, endHour, endMinute), title, description);
                    }
                }
                if (c != null){
                    day.addCourse(c);
                }
            }
            week.addDay(day);
        }
        CelcatParser.week = week;
    }

    public static void buildICS(){
        ics = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n";
        Hours hours;
        Date date;
        for(Day d : week.getDays()){
            for(Course c : d.getCourses()){
                hours = c.getHours();
                date = d.getDate();
                ics += "BEGIN:VEVENT\n" +
                "DTSTART:" + date.getYear() + date.getMonth() + date.getDay() + "T" + hours.getStartHour() + hours.getStartMinute() + "00Z\n" +
                "DTEND:" + date.getYear() + date.getMonth() + date.getDay() + "T" + hours.getEndHour() + hours.getEndMinute() + "00Z\n" +
                "DESCRIPTION:" + c.getDescription() + "\n" +
                "SEQUENCE:0\n" + //c quoi ca
                "STATUS:CONFIRMED\n" +
                "SUMMARY:" + c.getTitle() + "\n" +
                "END:VEVENT\n";
            }
        }
        ics += "END:VCALENDAR";
        Log.d("ics", ics);
    }

    public static void saveICS(){
        try{
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/celcat.ics");
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(ics);
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

