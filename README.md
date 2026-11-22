# Reef Log – app Android

App per la gestione dell'acquario marino: test, triade (Balling / All In One), consumi,
grafici, manutenzione, osmosi, animali e foto. Tutti i dati restano sul telefono.

## Come si compila
Ogni modifica caricata sul branch `main` avvia GitHub Actions, che produce `ReefLog.apk`
(scheda **Actions** → ultima esecuzione → sezione **Artifacts**).

## Struttura
- `www/` – l'app (HTML/JS), con Chart.js incluso per funzionare offline
- `res/` – icone e schermata di avvio, copiate nel progetto Android durante la compilazione
- `capacitor.config.json` – nome e identificativo dell'app (`it.reeflog.app`)
- `.github/workflows/android.yml` – compilazione automatica
