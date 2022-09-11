# [Updated version](https://github.com/Klbgr/EzStudies-Flutter)

# EzStudies [![Github All Releases](https://img.shields.io/github/downloads/Klbgr/EzStudies/latest/total.svg)](https://github.com/Klbgr/EzStudies/releases/latest)

![logo](./app/src/main/res/drawable/logo.png)
------
## Description

`EzStudies` is an application **made by students for students**. As a student at CYU, using that application can optimize your lifestyle and your workflow. Connect to Celcat, set your travel time and preparation time and the application does the rest.

## Guide

### Welcome

The first time you will start the app, you'll be greeted by this screen.
First page | Second page
:-:|:-:
![image](https://user-images.githubusercontent.com/43754408/150011406-273ac930-2279-47c8-8fe0-540f679bc267.png) | ![image](https://user-images.githubusercontent.com/43754408/150011424-b4153aea-52ad-4d9a-8b43-78662c22289c.png)

From there, you'll need to finish the setup before getting to the main screen of our app.

### Overview

This is the main screen, called Overview, which displays important informations.

![image](https://user-images.githubusercontent.com/43754408/150016616-0622a96b-346b-42e1-bc68-8089c67275b6.png)

On the top, we have a little text which tells you at what time you'll need to get up and how long your travel will be.
Then, there is a section showing today's courses and another section showing your unfinished homeworks.
You can click on each section to access to the Agenda or the Homeworks screen.

### Agenda

On this Agenda screen, you will be able to refresh your agenda and navigate through every days from monday to saturday.

![image](https://user-images.githubusercontent.com/43754408/150015996-3b5890ac-0c15-4867-803f-a4de3e4adc85.png)

To navigate between days, you have to swipe horizontally.
You can click on each cards to edit or delete a course.
If you need to refresh your agenda, you can either use the button at the bottom right or you can pull down.
If you wish to export your agenda to an ICS file (iCalendar), you can press the button at the bottom left.

### Homeworks

On this screen, you can manage your homeworks. A green card means it's finished, and a red card means it's unfinished.

![image](https://user-images.githubusercontent.com/43754408/150017086-f190257f-594f-4660-938e-76ca1f964709.png)

You can click on the button at the bottom right to add a homework, you can specify a title, a description and a due date.
You can click on each cards to delete it or set it to (un)finished.

### Course Editor

On this screen, you can edit everything about a course or you can delete it.

![image](https://user-images.githubusercontent.com/43754408/150020449-7ae1642f-defa-45aa-ba95-2b20cac0abbb.png)

### Settings

This is the Settings screen.

![image](https://user-images.githubusercontent.com/43754408/150014632-a1cfa32f-3b23-4a5d-998b-f04714877496.png)

Here, you have various clickable items :
- Set the theme (System, Light, Dark)
- Set the import mode of Agenda (from Celcat, from ics file)
  - Connect to Celcat Calendar
- Set your travel mode (car, walk, transit)
  - Set your home's address
  - Set your school's address
  - Set your travel time
- Set you preparation time (how much time do you need to get ready)
- Toggle automatic alarms (BETA, strongly not recommended)
- Send feedbacks / Report bugs
- Informations about us

### Map

When you need to set an address, you'll have to use this Map, which lets you select your address.

![image](https://user-images.githubusercontent.com/43754408/150014164-fe089928-0fa4-451e-a49b-eff5d61d54ae.png)

You can select an address by three ways :
- Using the search bar
- Clicking somewhere on the map
- Using your phone's GPS sensor (bottom-right button)

Once you placed your marker, you'll need to press "OK" to validate your choice.

## Notifications

Our app can send you three types of notifications :
- 15 minutes before each courses, a reminder for this course

![image](https://user-images.githubusercontent.com/43754408/150020125-9c275d2c-ff25-4de2-8e97-de5ed91f7ede.png)

- The day before the due date of a homework at 19:00, a reminder for this homework

![image](https://user-images.githubusercontent.com/43754408/150019784-142462a9-e04a-45a2-8686-cdee454f1722.png)

- Every sundays at 19:00, a reminder to refresh your agenda

![image](https://user-images.githubusercontent.com/43754408/150701427-6889159c-c2e7-4a17-a69e-dbeb37af2f4d.png)

## Widgets

Our app contains two widgets :
- Your next course

![image](https://user-images.githubusercontent.com/43754408/150019889-a1de3e16-5e82-4f35-8a2f-88c749fdfaec.png)

- Your next unfinished homework

![image](https://user-images.githubusercontent.com/43754408/150019853-2e5c2c36-460b-4c6a-8e39-89ddfa460dc7.png)

## Collaborators

Here are all the collaborators of this repository :
- [@Klbgr](https://github.com/Klbgr)
- [@Malinx95](https://github.com/Malinx95)
- [@IIyn](https://github.com/IIyn)

## Activities

Here are the activities of the app :

- [Welcome.java](./app/src/main/java/com/ezstudies/app/activities/Agenda.java)
- [WelcomeFragment.java ](./app/src/main/java/com/ezstudies/app/activities/WelcomeFragment.java)
- [Overview.java](./app/src/main/java/com/ezstudies/app/activities/Overview.java)
- [Agenda.java](./app/src/main/java/com/ezstudies/app/activities/Agenda.java)
- [AgendaFragment.java](./app/src/main/java/com/ezstudies/app/activities/AgendaFragment.java)
- [CourseEditor.java](./app/src/main/java/com/ezstudies/app/activities/CourseEditor.java)
- [Homeworks.java ](./app/src/main/java/com/ezstudies/app/activities/Homeworks.java )
- [MyMapView.java](./app/src/main/java/com/ezstudies/app/activities/MyMapView.java)

## Services

Here are the services of the app :
- [AlarmSetter.java](./app/src/main/java/com/ezstudies/app/services/AlarmSetter.java)
- [GPS.java](./app/src/main/java/com/ezstudies/app/services/GPS.java)
- [Login.java](./app/src/main/java/com/ezstudies/app/services/Login.java)
- [RouteCalculator.java](./app/src/main/java/com/ezstudies/app/services/RouteCalculator.java)
- [UpdateChecker.java](./app/src/main/java/com/ezstudies/app/services/UpdateChecker.java)

## Feedbacks / Issues

You can send a feedback or report an issue by using the dedicated section in our app's settings or you can use the Issues page of GitHub.

## External resources

Here are the external resources used for the app :
- [Jsoup library](https://jsoup.org/)
- [Route REST API by Bing Maps](https://docs.microsoft.com/en-us/bingmaps/rest-services/routes/calculate-a-route)
- [Get JSON object from url](https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java)
- Google Maps SDK for Android
