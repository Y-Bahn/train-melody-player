package io.ybahn.trainmelody;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class NewAppWidget extends AppWidgetProvider {

    public static final String ACTION_PLAY = "io.ybahn.trainmelody.widget.ACTION_PLAY";
    public static final String ACTION_UP = "io.ybahn.trainmelody.widget.ACTION_UP";
    public static final String ACTION_DOWN = "io.ybahn.trainmelody.widget.ACTION_DOWN";
    public static final String ACTION_STOP = "io.ybahn.trainmelody.widget.ACTION_STOP";
    public static final String ACTION_INIT = "io.ybahn.trainmelody.widget.ACTION_INIT";
    private static String[] paths;
    private static String[] titles;
    private static int position = 0;
    private static int max = 0;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        MusicDirectory musicDirectory = new MusicDirectory();
        titles = musicDirectory.getAllTitles();
        paths = musicDirectory.getAllPaths();
        max = titles.length;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        MusicDirectory musicDirectory = new MusicDirectory();
        titles = musicDirectory.getAllTitles();
        paths = musicDirectory.getAllPaths();
        max = titles.length;
        for (int appWidgetId : appWidgetIds) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), io.ybahn.trainmelody.R.layout.new_app_widget);
            views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text, titles[position]);

            Intent startIntent = new Intent(MyService.ACTION_PLAY);
            startIntent.setClass(context, MyService.class);
            startIntent.putExtra("path", paths[position]);
            startIntent.putExtra("title", titles[position]);
            PendingIntent startPendingIntent = PendingIntent.getService(context, 0, startIntent, 0);
            views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.appwidget_button_on, startPendingIntent);

            Intent stopIntent = new Intent(context, MyService.class);
            stopIntent.setAction(MyService.ACTION_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getService(context, appWidgetId, stopIntent, 0);
            views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.appwidget_button_off, stopPendingIntent);

            Intent upIntent = new Intent(context, NewAppWidget.class);
            upIntent.setAction(ACTION_UP);
            upIntent.putExtra("value", String.valueOf(position));
            PendingIntent upPendingIntent = PendingIntent.getBroadcast(context, appWidgetId ,upIntent, 0);
            views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.button2, upPendingIntent);

            Intent downIntent = new Intent(context, NewAppWidget.class);
            downIntent.setAction(ACTION_DOWN);
            downIntent.putExtra("value", String.valueOf(position));
            PendingIntent downPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, downIntent, 0);
            views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.button3, downPendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, NewAppWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);



        for (int appWidgetId:appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), io.ybahn.trainmelody.R.layout.new_app_widget);
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    String title = intent.getExtras().getString("title");
                    views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text2, title);
                    break;
                case ACTION_UP:
                    position++;
                    if (position >= max) {
                        position = 0;
                    }
                    views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text, titles[position]);

                    Intent startIntent = new Intent(MyService.ACTION_PLAY);
                    startIntent.setClass(context, MyService.class);
                    startIntent.putExtra("path", paths[position]);
                    startIntent.putExtra("title", titles[position]);
                    PendingIntent startPendingIntent = PendingIntent.getService(context, appWidgetId, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.appwidget_button_on, startPendingIntent);
                    break;
                case ACTION_DOWN:
                    position--;
                    if (position < 0) {
                        position = max - 1;
                    }
                    views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text, titles[position]);

                    Intent startIntent2 = new Intent(MyService.ACTION_PLAY);
                    startIntent2.setClass(context, MyService.class);
                    startIntent2.putExtra("path", paths[position]); //選曲しても再生される曲が変わらない問題は.FLAG_CANCEL_CURRENTで解決できる
                    startIntent2.putExtra("title", titles[position]);
                    PendingIntent startPendingIntent2 = PendingIntent.getService(context, appWidgetId, startIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
                    views.setOnClickPendingIntent(io.ybahn.trainmelody.R.id.appwidget_button_on, startPendingIntent2);
                    break;
                case ACTION_STOP:
                    views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text2, "");
                    break;
                case ACTION_INIT:
                    MusicDirectory musicDirectory = new MusicDirectory();
                    titles = musicDirectory.getAllTitles();
                    paths = musicDirectory.getAllPaths();
                    max = titles.length;
                    views.setTextViewText(io.ybahn.trainmelody.R.id.appwidget_text, titles[position]);
                default:
                    break;
            }
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }
}

