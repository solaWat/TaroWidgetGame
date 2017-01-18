package jp.gr.java_conf.TaroIchinose;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Implementation of App Widget functionality.
 */
public class MainWidget extends AppWidgetProvider {
    private static final String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    static private boolean mRunning = false;
    private static AnimeTask mTask;
    private static Timer mTimer;
    private static final String TAG = "TestWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        views.setOnClickPendingIntent(R.id.button, pushButton(context));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        switch (intent.getAction()) {
            case ACTION_UPDATE:
                Log.d(TAG, "get ACTION: UPDATE");
                mRunning = !mRunning;
                if (mRunning) {
                    mTask = new AnimeTask(context);
                    mTimer = new Timer(true);
                    mTimer.schedule(mTask, 100, 1000 / 60);
                } else {
                    mTimer.cancel();
                    mTimer = null;
                }
                break;
            default:
                break;
        }
    }

    static private PendingIntent pushButton(Context context) {
        Log.d(TAG, "pushButton");

        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private class AnimeTask extends TimerTask {
        AppWidgetManager mManager;
        int[] mAppWidgetIds;
        RemoteViews mViews;
        int mCount = 0;
        int LocationX = 0;

        public AnimeTask(Context context) {
            super();

            mViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            mManager = AppWidgetManager.getInstance(context);
            ComponentName myWidget = new ComponentName(context, MainWidget.class);
            mAppWidgetIds = mManager.getAppWidgetIds(myWidget);
        }

        @Override
        public void run() {
            mCount++;
            if(LocationX > 800) LocationX += 10;

            mViews.setTextViewText(R.id.textView, "Count: " + mCount);
            mViews.setViewPadding(R.id.enemy, 0, 0, LocationX, 0);
            mManager.updateAppWidget(mAppWidgetIds, mViews);
        }
    }
}
