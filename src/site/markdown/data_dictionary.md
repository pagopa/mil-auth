# Strutture dati

## Data dictionary
| Etichetta                                 | Descrizione                                                                                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `<access token duration>`                 | Durata in secondi dell'access token.                                                                                                                          |
| `<access token hash>`                     | Hash dell'access token su cui calcolare la firma.                                                                                                             |
| `<access token header>`                   | Header dell'access token.                                                                                                                                     |
| `<access token payload>`                  | Payload dell'access token.                                                                                                                                    |
| `<access token with token_info role>`     | Access token adoperato del servizio che invoca l'end-point token_info.                                                                                        |
| `<access token>`                          | Access token generato.                                                                                                                                        |
| `<acquirer id>`                           | Codice ABI della Banca dell'ATM.                                                                                                                              |
| `<api version>`                           | Versione delle API richieste (al momento inutilizzato).                                                                                                       |
| `<b64 fiscal code>`                       | Codifica UTF-8 del codice fiscale in chiaro rappresentato in Base64 URL-safe.                                                                                 |
| `<channel>`                               | `ATM`                                                                                                                                                         |
| `<client id>`                             | ID del client inviato dal servizio richiedente l'access token.                                                                                                |
| `<client secret>`                         | Secret assegnato al servizio che richiede l'access token.                                                                                                     |
| `<cryptoperiod>`                          | Durata in secondi della chiave.                                                                                                                               |
| `<current unix epoch>`                    | Timestamp corrente, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.                                                          |
| `<description>`                           | Descrizione del client.                                                                                                                                       |
| `<enc fiscal code>`                       | Codice fiscale cifrato rappresentato in Base64 URL-safe.                                                                                                      |
| `<enc/dec key azure id>`                  | Key ID per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                                              |
| `<enc/dec key creation unix epoch>`       | Timestamp di creazione della chiave per cifrare/decifrare il codice fiscale, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC. |
| `<enc/dec key expiration unix epoch>`     | Timestamp di scadenza della chiave per cifrare/decifrare il codice fiscale, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.  |
| `<enc/dec key modulus>`                   | Modulo della chiave per cifrare/decifrare il codice fiscale.                                                                                                  |
| `<enc/dec key my id>`                     | Key ID per mil-auth della chiave per cifrare/decifrare il codice fiscale.                                                                                     |
| `<enc/dec key name>`                      | Nome della chiave per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                                   |
| `<enc/dec key public exponent>`           | Esponente pubblico della chiave per cifrare/decifrare il codice fiscale.                                                                                      |
| `<enc/dec key version>`                   | Versione della chiave per Azure Key Vault della chiave per cifrare/decifrare il codice fiscale.                                                               |
| `<fiscal code>`                           | Codice fiscale in chiaro.                                                                                                                                     |
| `<key size>`                              | Lunghezza in bit del modulo della chiave.                                                                                                                     |
| `<mil-auth url>`                          | URL di mil-auth.                                                                                                                                              |
| `<request id>`                            | ID della richiesta.                                                                                                                                           |
| `<role #1>`                               | primo ruolo da inserire nell'access token.                                                                                                                    |
| `<role #2>`                               | secondo ruolo da inserire nell'access token.                                                                                                                  |
| `<role #n>`                               | n-esimo ruolo da inserire nell'access token.                                                                                                                  |
| `<salt>`                                  | Salt adoperato per calcolare il salted hash del client secret.                                                                                                |
| `<secret hash>`                           | Salted hash del client secret.                                                                                                                                |
| `<sign/verify key azure id>`              | Key ID per Azure Key Vault della chiave per firmare/verificare l'access token.                                                                                |
| `<sign/verify key creation unix epoch>`   | Timestamp di creazione della chiave per firmare/verificare l'access token, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.   |
| `<sign/verify key expiration unix epoch>` | Timestamp di scadenza della chiave per firmare/verificare l'access token, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.    |
| `<sign/verify key modulus>`               | Modulo della chiave per firmare/verificare l'access token.                                                                                                    |
| `<sign/verify key my id>`                 | Key ID per mil-auth della chiave per firmare/verificare l'access token.                                                                                       |
| `<sign/verify key name>`                  | Nome della chiave per Azure Key Vault della chiave per firmare/verificare l'access token.                                                                     |
| `<sign/verify key public exponent>`       | Esponente pubblico della chiave per firmare/verificare l'access token.                                                                                        |
| `<sign/verify key version>`               | Versione della chiave per Azure Key Vault della chiave per firmare/verificare l'access token.                                                                 |
| `<signature>`                             | Firma dell'access token.                                                                                                                                      |
| `<stored channel>`                        | Canale assegnato al client.                                                                                                                                   |
| `<stored client id>`                      | ID del client recuperato dal repository.                                                                                                                      |
| `<subject>`                               | Se il servizio richiedente è una generica applicazione server, indica opzionalmente il subject id da inserire nei token.                                      |
| `<subject type>`                          | Se il servizio richiedente è una generica applicazione server, indica opzionalmente il tipo subject da inserire nei token.                                    |
| `<terminal id>`                           | ID dell'ATM.                                                                                                                                                  |
| `<token to introspect>`                   | Access token da cui estrarre il codice fiscale.                                                                                                               |
| `<issue unix epoch>`                      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.                            |
| `<expiration unix epoch>`                 | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1/1/1970 nel fuso orario UTC.                                                  |
| `<host name>`                             | Nome host cui è raggiungibile mil-auth.                                                                                                                       |

## Struttura del payload degli access token
> La presenza del claim `fiscalCode` dipende dal fatto che nella richiesta dell'access token sia specificato un codice fiscale.

### ATM Layer
```json
{
	"sub": "<acquirer id>/<terminal id>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"clientId": "<client id>",
	"channel": "ATM",
	"acquirerId": "<acquirer id>",
	"terminalId": "<terminal id>",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"groups": [
		"<role #1>",
		"<role #2>",
		"<role #n>"
	],
	"fiscalCode": {
		"kid": "<enc/dec key my id>",
		"alg": "RSA-OAEP-256",
		"value": "<enc fiscal code>"
  	}
}
```

### Altre applicazioni server
```json
{
	"sub": "<subject>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"clientId": "<client id>",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"groups": [
		"<role #1>",
		"<role #2>",
		"<role #n>"
	],
	"fiscalCode": {
		"kid": "<enc/dec key my id>",
		"alg": "RSA-OAEP-256",
		"value": "<enc fiscal code>"
  	}
}
```

## Struttura dell'Entity Client
> `<subject>` indica l’utente, l’organizzazione o il servizio cui è stato rilasciato il token, è immutabile, non può essere riassegnato o riutilizzato. `<client id>` indica l’applicazione registrata su mil-auth e quindi abilitata a richiedere gli access token.

### ATM Layer
```json
{
	"clientId": "<client id>",
	"channel": "ATM",
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"description": "<description>"
}
```

### Altre applicazioni server
```json
{
	"clientId": "<client id>",
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"description": "<description>",
	"subject": "<subject>"
}
```

## Struttura dell'Entity Roles

### ATM Layer

#### Ruoli per tutti gli ATM di una specifica Banca
```json
{
	"acquirerId": "<acquirer id>",
	"channel": "ATM",
	"clientId": "<client id>",
	"merchantId": "NA",
	"terminalId": "NA",
	"roles": [
		"<role #1>",
		"<role #2>",
		"<role #n>"
	]
}
```

#### Ruoli per uno specifico ATM
```json
{
	"acquirerId": "<acquirer id>",
	"channel": "ATM",
	"clientId": "<client id>",
	"merchantId": "NA",
	"terminalId": "<terminal id>",
	"roles": [
		"<role #1>",
		"<role #2>",
		"<role #n>"
	]
}
```

### Altre applicazioni server
```json
{
	"acquirerId": "NA",
	"channel": "NA",
	"clientId": "<client id>",
	"merchantId": "NA",
	"terminalId": "NA",
	"roles": [
		"<role #1>",
		"<role #2>",
		"<role #n>"
	]
}
```

## Ruoli attualmente registrati
| Ruoli                   | Descrizione |
| ----------------------- | ----------- |
| `emd-tpp`               |             |
| `EnrollToIDPay`         |             |
| `InstitutionPortal`     |             |
| `mil_papos_admin`       |             |
| `mil-auth-admin`        |             |
| `Nodo`                  |             |
| `NoticePayer`           |             |
| `PayWithIDPay`          |             |
| `pos_service_provider`  |             |
| `public_administration` |             |
| `read_rtp_activations`  |             |
| `send`                  |             |
| `SlavePos`              |             |
| `token_info`            |             |
| `write_rtp_activations` |             |
| `write_rtp_send`        |             |
| `write_idpay_bonus_io`  |             |