# Strutture dati

## Struttura del payload degli access token

### POS
```json
{
	"sub": "<terminal uuid>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"payeeCode": "<payee code>",
	"serviceProviderId": "<service provider id>",
	"channel": "POS",
	"terminalHandlerId": "<terminal handler id>",
	"terminalId": "<terminal id>",
	"groups": [ <array of roles> ],
	"pagoPaConf": {
		"pspId": "<psp id>"
		"brokerId": "<broker id>"
		"channelId": "<channel id>"
	}
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<terminal uuid>`         | ID univoco per mil-auth assegnato al POS.                                                                                                 |
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<service provider id>`   | ID del service provider che gestisce il POS.                                                                                              |
| `<payee code>`            | Codice fiscale dell’ente o dell’esercente presso cui opera il POS.                                                                        |
| `<terminal handler id>`   | ID del Gestore Terminali cui è collegata l’app CB2 del POS.                                                                               |
| `<terminal id>`           | ID del POS per il Gestore Terminali.                                                                                                      |
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.                                                                                |
| `<psp id>`                | PSP ID da adoperare nella comunicazione con il Nodo pagoPA. Presente solo se il POS è abilitato al pagamento degli avvisi pagoPA.         |
| `<broker id>`             | Broker ID da adoperare nella comunicazione con il Nodo pagoPA. Presente solo se il POS è abilitato al pagamento degli avvisi pagoPA.      |
| `<channel id>`            | Channel ID da adoperare nella comunicazione con il Nodo pagoPA. Presente solo se il POS è abilitato al pagamento degli avvisi pagoPA.     |

### ATM Layer
```json
{
	"sub": "<bank id> || <terminal id>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"channel": "ATM",
	"bankId": "<bank id>",
	"terminalId": "<terminal id>",
	"userCodeToken": "<token of user code>",
	"groups": [ <array of roles> ]
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<bank id>`               | Codice ABI assegnato alla Banca proprietaria dell’ATM.                                                                                    |
| `<terminal id>`           | ID univoco dell’ATM per la Banca.                                                                                                         |
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<token of user code>`    | Claim opzionale contenete il token generato tramite PDV del codice fiscale dell’Utente che sta adoperando l’ATM.                          |
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.                                                                                |

### Gestore POS, Ente, MIL, Altre applicazioni server
```json
{
	"sub": "<subject id>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"channel": "<channel>",
	"groups": [ <array of roles> ]
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<channel>`               | I possibili valori sono: `POS_SERVICE_PROVIDER`, `PUBLIC_ADMINISTRATION`, `MIL`, `SERVER`                                                 |
| `<subject id>`            | Il contenuto dipende da `<channel>` (vedi [`<channel>`/`<subject id>`](#channelsubject-id)).                                              |
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.                                                                                |

#### `<channel>`/`<subject id>`
| `<channel>`             | `<subject id>`                   | Descrizione                                                               |
| ----------------------- | -------------------------------- | ------------------------------------------------------------------------- |
| `POS_SERVICE_PROVIDER`  | `<service provider id>`          | ID univoco per mil-auth assegnato al Service Provider che gestisce i POS. |
| `PUBLIC_ADMINISTRATION` | `<payee code>`                   | Codice fiscale dell’ente.                                                 |
| `MIL`                   | `<microservice>`                 | Nome del microservizio del MIL: `mil-auth` oppure `mil-payment-notice`.   |
| `SERVER`                | `<application name> `            | Nome dell'applicazione server.                                            |

## Struttura del payload dei refresh token

### POS
```json
{
	"jti": "<refresh token id>",
	"sub": "<terminal uuid>",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"channel": "POS",
	"scope": "offline_access"
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<refresh token id>`      | ID del refresh token.                                                                                                                     |
| `<terminal uuid>`         | ID univoco per mil-auth assegnato al POS.                                                                                                 |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<scope>`                 | `offline_access`                                                                                                                          |

## Subject vs Client ID
Il **subject** indica l’utente, l’organizzazione o il servizio cui è stato rilasciato il token, è immutabile, non può essere riassegnato o riutilizzato. Il subject è presente nei token, in particolare nel claim `sub`.

Il **client id** indica l’applicazione registrata su mil-auth e quindi abilitata a richiedere gli access token.

## Client (Applicazione)
Segue la documentazione dei client descriptor.

### POS
```json
{
	"id": "<client id>",
	"channel": "POS",
	"grantTypes": [
		"device_code",
		"refresh_token"
	]
}
```

| Dato          | Descrizione                                                    |
| ------------- | -------------------------------------------------------------- |
| `<client id>` | Indica l’applicazione abilitata a richiedere gli access token. |

### ATM Layer
```json
{
	"id": "<client id>",
	"channel": "ATM",
	"grantTypes": [ 
		"client_credentials"
	],
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"secretExp": <secret expiration unix epoch>
}
```

| Dato                             | Descrizione                                                                                                                   |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `<client id>`                    | Indica l’applicazione abilitata a richiedere gli access token.                                                                |
| `<salt>`                         | Salt da adoperare per calcolare l’hash del client secret assegnato.                                                           |
| `<secret hash>`                  | Hash del client secret assegnato.                                                                                             |
| `<secret expiration unix epoch>` | Scadenza del client secret assegnato, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |

### Gestore POS, Ente, MIL, Altre applicazioni server
```json
{
	"id": "<client id>",
	"channel": "<channel>",
	"grantTypes": [
		"client_credentials"
	],
	"sub": "<subject id>",
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"secretExp": <secret expiration unix epoch>
}
```

| Dato                             | Descrizione                                                                                                                   |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `<client id>`                    | Indica l’applicazione abilitata a richiedere gli access token.                                                                |
| `<channel>`                      | I possibili valori sono: `POS_SERVICE_PROVIDER`, `PUBLIC_ADMINISTRATION`, `MIL`, `SERVER`                                     |
| `<subject id>`                   | Il contenuto dipende da `<channel>` (vedi [`<channel>`/`<subject id>`](#channelsubject-id)).                                  |
| `<salt>`                         | Salt da adoperare per calcolare l’hash del client secret assegnato.                                                           |
| `<secret hash>`                  | Hash del client secret assegnato.                                                                                             |
| `<secret expiration unix epoch>` | Scadenza del client secret assegnato, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |

## Gestione dei ruoli

### POS
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `POS`.

I ruoli sono indicati dall’anagrafica dei terminali (mil-terminal-registry).

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `NoticePayer`
* `PayWithIDPay`
* `SlavePos`

### ATM Layer
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `ATM`.

I ruoli da adoperare sono ricercati con i seguenti step:
1. ricerca dei ruoli nel file `atms/<bank id>/<terminal id>/roles.json`;
2. se la ricerca dello step precedente non ha dato risultati, ricerca dei ruoli nel file `atms/<bank id>/roles.json`;
3. se la ricerca dello step precedente non ha dato risultati, ricerca dei ruoli nel file `atms/roles.json`.

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `NoticePayer`
* `EnrollToIDPay`

### Gestore POS
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `POS_SERVICE_PROVIDER`.

I ruoli sono indicati nel file `pos_service_providers/<service provider id>.json`

I ruoli sono indicati dall’anagrafica dei terminali (mil-terminal-registry).

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `pos_service_provider`

### Ente
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `PUBLIC_ADMINISTRATION`.

I ruoli sono indicati nel file `public_administrations/<payee code>.json`

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `public_administration`

### MIL
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `MIL`.

I ruoli sono indicati nel file `mil_services/<microservice>.json`

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `pos_finder`

### Altre applicazioni server
In questo caso il client descriptor contiene l’attributo `channel` valorizzato con `SERVER`.

I ruoli sono indicati nel file `servers/<client id>.json`

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `atm_access_token_introspector`