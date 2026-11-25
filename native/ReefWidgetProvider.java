package it.reeflog.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReefWidgetProvider extends AppWidgetProvider {

    static final String PREFS = "reefwidget";
    static final int[] NAMES = { R.id.w_n1, R.id.w_n2, R.id.w_n3, R.id.w_n4, R.id.w_n5, R.id.w_n6, R.id.w_n7 };
    static final int[] VALUES = { R.id.w_v1, R.id.w_v2, R.id.w_v3, R.id.w_v4, R.id.w_v5, R.id.w_v6, R.id.w_v7 };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager manager, int[] ids) {
        for (int id : ids) manager.updateAppWidget(id, build(ctx));
    }

    static void updateAll(Context ctx) {
        AppWidgetManager manager = AppWidgetManager.getInstance(ctx);
        int[] ids = manager.getAppWidgetIds(new ComponentName(ctx, ReefWidgetProvider.class));
        for (int id : ids) manager.updateAppWidget(id, build(ctx));
    }

    static RemoteViews build(Context ctx) {
        RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.reef_widget);
        Intent open = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        if (open != null) {
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.w_root, pi);
        }
        String raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("data", null);
        try {
            if (raw == null) return message(v, "Apri Reef Log per attivare il widget");
            JSONObject d = new JSONObject(raw);
            v.setTextViewText(R.id.w_title, d.optString("tank", "Reef Log"));
            if (!d.optBoolean("pro", false)) return message(v, "Il widget è una funzione di Reef Log Pro");
            v.setViewVisibility(R.id.w_vals, View.VISIBLE);
            v.setViewVisibility(R.id.w_msg, View.GONE);
            JSONArray ps = d.optJSONArray("params");
            for (int i = 0; i < NAMES.length; i++) {
                JSONObject p = (ps != null && i < ps.length()) ? ps.optJSONObject(i) : null;
                v.setTextViewText(NAMES[i], p == null ? "" : p.optString("n") + (p.optString("u").isEmpty() ? "" : " " + p.optString("u")));
                v.setTextViewText(VALUES[i], p == null ? "–" : p.optString("v", "–"));
                v.setTextColor(VALUES[i], color(p == null ? "" : p.optString("s")));
            }
            long lt = d.optLong("lastTest", 0);
            v.setTextViewText(R.id.w_line1, lt > 0 ? "Ultimo test: " + ago(lt) : "Nessun test ancora");
            JSONObject nx = d.optJSONObject("next");
            v.setTextViewText(R.id.w_line2, nx == null ? "" : nx.optString("n") + " · " + due(nx.optLong("ts", 0)));
        } catch (Exception e) {
            return message(v, "Apri Reef Log per aggiornare il widget");
        }
        return v;
    }

    static RemoteViews message(RemoteViews v, String text) {
        v.setViewVisibility(R.id.w_vals, View.GONE);
        v.setViewVisibility(R.id.w_msg, View.VISIBLE);
        v.setTextViewText(R.id.w_msg, text);
        v.setTextViewText(R.id.w_line1, "");
        v.setTextViewText(R.id.w_line2, "");
        return v;
    }

    static int color(String s) {
        if ("ok".equals(s)) return Color.parseColor("#5fd49a");
        if ("warn".equals(s)) return Color.parseColor("#ffd166");
        if ("bad".equals(s)) return Color.parseColor("#ff7b7b");
        return Color.parseColor("#e6f1f5");
    }

    static long day0(long ts) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    static long daysFromToday(long ts) {
        return Math.round((day0(ts) - day0(System.currentTimeMillis())) / 86400000.0);
    }

    static String ago(long ts) {
        long d = -daysFromToday(ts);
        if (d <= 0) return "oggi";
        if (d == 1) return "ieri";
        return d + " giorni fa";
    }

    static String due(long ts) {
        long d = daysFromToday(ts);
        if (d < 0) return "in ritardo di " + (-d) + (d == -1 ? " giorno" : " giorni");
        if (d == 0) return "oggi";
        if (d == 1) return "domani";
        return "tra " + d + " giorni";
    }
}
