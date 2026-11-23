# Aggiunge al manifest generato da Capacitor: widget e regole del backup automatico
import re, sys
p = 'android/app/src/main/AndroidManifest.xml'
s = open(p).read()
if 'ReefWidgetProvider' not in s:
    recv = '''
        <receiver android:name=".ReefWidgetProvider" android:exported="false">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data android:name="android.appwidget.provider" android:resource="@xml/reef_widget_info" />
        </receiver>
    </application>'''
    s = s.replace('</application>', recv, 1)
s = re.sub(r'android:allowBackup="[^"]*"', '', s)
s = s.replace('<application', '<application android:allowBackup="true" android:fullBackupContent="@xml/backup_rules" android:dataExtractionRules="@xml/data_extraction_rules"', 1)
open(p, 'w').write(s)
print(s)
