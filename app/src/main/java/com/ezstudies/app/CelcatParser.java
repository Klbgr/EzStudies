package com.ezstudies.app;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

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

public class CelcatParser extends AppCompatActivity {
    private WebView webview;
    private JavaScriptInterface jsi;
    private Week week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebview();
        login();
    }

    @Override
    protected void onStop() {
        super.onStop();
        parse();
        saveICS();
    }

    public void initWebview() {
        webview = new WebView(this);
        webview.setWebViewClient(new Webview());
        webview.getSettings().setJavaScriptEnabled(true);
        setContentView(webview);

    }

    public void login() {
        jsi = new JavaScriptInterface();
        webview.addJavascriptInterface(jsi, "HTMLOUT");
        String url = "https://services-web.u-cergy.fr/calendar/cal?vt=agendaWeek";
        webview.loadUrl(url);
    }

    public void parse() {
        String source = jsi.getSource();
        ArrayList<String> daysList = new ArrayList(); //days of month
        Document document = Jsoup.parse(source, "UTF-8");
        Elements days = document.select("a[data-goto]");
        for (Element day : days) { //days
            Elements headerDays = day.getElementsByTag("a");
            for (Element headerDay : headerDays) {
                String currentDay = headerDay.text();
                daysList.add(currentDay);
            }
        }

        //Log.d("days", daysList.toString());

        Elements rows = document.getElementsByClass("fc-content-skeleton");
        Element row = rows.get(1);
        Element now = row.getElementsByClass("fc-axis").get(0);

        Week week = new Week();
        int year = Calendar.getInstance().get(1);

        for (int i = 0; i <=5; i++){
            String date[] = daysList.get(i).split(" ")[1].split("/");
            Day day = new Day(new Date(Integer.parseInt(date[0]), Integer.parseInt(date[1]), year));
            now = now.nextElementSibling();
            Elements events = now.getElementsByClass("fc-content");
            for (Element event : events){
                String hours = event.getElementsByTag("span").get(0).text();

                String start = hours.substring(0, hours.indexOf("-") - 1);
                String end = hours.substring(hours.indexOf("-") + 2);

                String string = event.toString();

                string = string.substring(string.indexOf("</div>") + 6, string.lastIndexOf("</div>"));

                String type = string.substring(0, string.indexOf("<br>") - 1);

                string = string.substring(string.indexOf("<br>") + 5);

                String title = string.substring(0, string.indexOf("<br>") - 1);

                string = string.substring(string.indexOf("<br>") + 5);

                String place = string.substring(0, string.indexOf("<br>") - 1);

                string = string.substring(string.indexOf("<br>") + 5);

                String teacher = string;

                int startHour = Integer.parseInt(start.split(":")[0]);
                int startMinute = Integer.parseInt(start.split(":")[1]);
                int endHour = Integer.parseInt(end.split(":")[0]);
                int endMinute = Integer.parseInt(end.split(":")[1]);

                ArrayList<String> teachers= new ArrayList<>();
                if(teacher.contains("<br>")){
                    String[] teacherList = teacher.split("<br>");
                    for (String s : teacherList) {
                        teachers.add(s.replace("\n", ""));
                    }
                }
                else{
                    teachers.add(teacher.replace("\n", ""));
                }

                Course c = new Course(new Hours(startHour, startMinute, endHour, endMinute), type.replace("\n", ""), title.replace("\n", ""), place.replace("\n", ""), teachers);
                day.addCourse(c);
                Log.d("course", c.toString());
            }
            week.addDay(day);
        }
        this.week = week;
    }

    public void saveICS(){
        String ics = "BEGIN:VCALENDAR\n" +
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
                "DESCRIPTION:" + c.getTeachers() + "\n" +
                "LOCATION:" + c.getPlace() + "\n" +
                "SEQUENCE:0\n" + //c quoi ca
                "STATUS:CONFIRMED\n" +
                "SUMMARY:" + c.getType() + " - " + c.getTitle() + "\n" +
                "END:VEVENT\n";
            }
        }
        ics += "END:VCALENDAR";
        Log.d("ics", ics);
        /*
        try{
            File file = new File("celcat.ics");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(ics);
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

         */
    }
}

