package it.reeflog.app;

import android.content.Context;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ReefWidget")
public class ReefWidgetPlugin extends Plugin {

    @PluginMethod
    public void update(PluginCall call) {
        String data = call.getString("data");
        if (data == null) {
            call.reject("data mancante");
            return;
        }
        Context ctx = getContext();
        ctx.getSharedPreferences(ReefWidgetProvider.PREFS, Context.MODE_PRIVATE).edit().putString("data", data).apply();
        ReefWidgetProvider.updateAll(ctx);
        call.resolve();
    }
}
