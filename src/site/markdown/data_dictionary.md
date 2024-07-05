# Data Dictionary

| Etichetta                                 | Descrizione                                                                                                                                           |
| ----------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `<access token duration>`                 | Durata in secondi dell'access token.                                                                                                                  |
| `<access token hash>`                     | Hash dell'access token su cui calcolare la firma.                                                                                                     |
| `<access token header>`                   | Header dell'access token.                                                                                                                             |
| `<access token payload>`                  | Payload dell'access token.                                                                                                                            |
| `<access token with token_info role>`     | Access token adoperato del servizio che invoca l'end-point token_info.                                                                                |
| `<access token>`                          | Access token generato.                                                                                                                                |
| `<acquirer id>`                           | Codice ABI della Banca dell'ATM.                                                                                                                      |
| `<api version>`                           | Versione delle API richieste (al momento inutilizzato).                                                                                               |
| `<b64 fiscal code>`                       | Codifica UTF-8 del codice fiscale in chiaro rappresentato in Base64 URL-safe.                                                                         |
| `<channel>`                               | `ATM`                                                                                                                                                 |
| `<client id>`                             | ID del client inviato dal servizio richiedente l'access token.                                                                                        |
| `<client secret>`                         | Secret assegnato al servizio che richiede l'access token.                                                                                             |
| `<cryptoperiod>`                          | Durata in secondi della chiave.                                                                                                                       |
| `<current unix epoch>`                    | Timestamp corrente, espresso in numero di secondi a parire dal 1/1/1970 nel fuso orario UTC.                                                          |
| `<description>`                           | Descrizione del client.                                                                                                                               |
| `<enc fiscal code>`                       | Codice fiscale cifrato rappresentato in Base64 URL-safe.                                                                                              |
| `<enc/dec key azure id>`                  | Key ID per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                                      |
| `<enc/dec key creation unix epoch>`       | Timestamp di creazione della chiave per cifrare/decifrare il codice fiscale, espresso in numero di secondi a parire dal 1/1/1970 nel fuso orario UTC. |
| `<enc/dec key expiration unix epoch>`     | Timestamp di scadenza della chiave per cifrare/decifrare il codice fiscale, espresso in numero di secondi a parire dal 1/1/1970 nel fuso orario UTC.  |
| `<enc/dec key modulus>`                   | Modulo della chiave per cifrare/decifrare il codice fiscale.                                                                                          |
| `<enc/dec key my id>`                     | Key ID per mil-auth della chiave per cifrare/decifrare il codice fiscale.                                                                             |
| `<enc/dec key name>`                      | Nome della chiave per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                           |
| `<enc/dec key public exponent>`           | Esponente pubblico della chiave per cifrare/decifrare il codice fiscale.                                                                              |
| `<enc/dec key version>`                   | Versione della chiave per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                       |
| `<fiscal code>`                           | Codice fiscale in chiaro.                                                                                                                             |
| `<key size>`                              | Lunghezza in bit del modulo della chiave.                                                                                                             |
| `<mil-auth url>`                          | URL di mil-auth.                                                                                                                                      |
| `<request id>`                            | ID della richiesta.                                                                                                                                   |
| `<role #1>`                               | primo ruolo da inserire nell'access token.                                                                                                            |
| `<role #2>`                               | secondo ruolo da inserire nell'access token.                                                                                                          |
| `<role #n>`                               | n-esimo ruolo da inserire nell'access token.                                                                                                          |
| `<salt>`                                  | Salt adoperato per calcolare il salted hash del client secret.                                                                                        |
| `<secret hash>`                           | Salted hash del client secret.                                                                                                                        |
| `<sign/verify key azure id>`              | Key ID per Azure Key Vault della chiave per firmare/verificare l'access token.                                                                        |
| `<sign/verify key creation unix epoch>`   | Timestamp di creazione della chiave per firmare/verificare l'access token, espresso in numero di secondi a parire dal 1/1/1970 nel fuso orario UTC.   |
| `<sign/verify key expiration unix epoch>` | Timestamp di scadenza della chiave per firmare/verificare l'access token, espresso in numero di secondi a parire dal 1/1/1970 nel fuso orario UTC.    |
| `<sign/verify key modulus>`               | Modulo della chiave per firmare/verificare l'access token.                                                                                            |
| `<sign/verify key my id>`                 | Key ID per mil-auth della chiave per firmare/verificare l'access token.                                                                               |
| `<sign/verify key name>`                  | Nome della chiave per Azure Key Vault della chiave per firmare/verificare l'access token.                                                             |
| `<sign/verify key public exponent>`       | Esponente pubblico della chiave per firmare/verificare l'access token.                                                                                |
| `<sign/verify key version>`               | Versione della chiave per Azure Key Vault della chiave per firmare/verificare l'access token.                                                         |
| `<signature>`                             | Firma dell'access token.                                                                                                                              |
| `<stored channel>`                        | Canale assegnato al client.                                                                                                                           |
| `<stored client id>`                      | ID del client recuperato dal repository.                                                                                                              |
| `<terminal id>`                           | ID dell'ATM.                                                                                                                                          |
| `<token to introspect>`                   | Access token da cui estrarre il codice fiscale.                                                                                                       |
