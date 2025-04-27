# Vypracovaný úkol
Před spuštěním vytvořte nebo překopírujte do rootu projektu `.env` soubor (podle přiloženého `.env.template` souboru), obsahující ID a secret Google OAuth2 klienta.

## Endpointy
- GET `http://localhost:8080/health`: Health check aplikace.
- GET `http://localhost:8080/currencies/pairs`: Zobrazí seznam povolených párů měn.
- GET `http://localhost:8080/currencies/rates/{sourceCurrency}/{targetCurrency}`, např. `http://localhost:8080/currencies/rates/eur/czk`: Pokud je tento pár měn povolený, zobrazí informace o převodu měn z kurzovních lístků a jejich porovnání. Data se používají z veřejných API:
  - ČNB: https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml
  - Ze seznamu https://github.com/public-apis/public-apis?tab=readme-ov-file#currency-exchange currency-api (https://github.com/fawazahmed0/currency-api), resp. jeho náhrada exchange-api (https://github.com/fawazahmed0/exchange-api)
