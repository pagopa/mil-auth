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

### Gestore POS
```json
{
	"sub": "<service provider id>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"groups": [ <array of roles> ]
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | 
| `<service provider id>`   | ID univoco per mil-auth assegnato al Service Provider che gestisce i POS.                                                                 |
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.                                                                                |

### Ente
```json
{
	"sub": "<payee code>",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"groups": [ <array of roles> ]
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| `<payee code>`            | Codice fiscale dell’ente.                                                                                                                 |
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.

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
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.

### mil-auth
> [!NOTE]
> Per la comunicazione con altri microservizi del MIL.

```json
{
	"sub": "mil-auth",
	"aud": "mil.pagopa.it",
	"iss": "https://<host name>/mil-auth",
	"iat": <issue unix epoch>,
	"exp": <expiration unix epoch>,
	"groups": [ <array of roles> ]
}
```

| Dato                      | Descrizione                                                                                                                               |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | 
| `<host name>`             | Nome host cui è raggiungibile mil-auth.                                                                                                   |
| `<issue unix epoch>`      | Istante in cui è stato rilasciato l’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |
| `<expiration unix epoch>` | Scadenza dell’access token, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC.                       |
| `<array of roles>`        | Array di stringhe indicanti i ruoli per implementare RBAC.                                                                                |

### mil-payment-notice
TODO

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

Il **client** id indica l’applicazione registrata su mil-auth e quindi abilitata a richiedere gli access token.

## Client (Applicazione)
Segue la documentazione dei client descriptor.

### POS
```json
{
	"id": "<client id>",
	"type": "POS",
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

### Gestore POS
```json
{
	"id": "<client id>",
	"type": "POS_SERVICE_PROVIDER",
	"grantTypes": [
		"client_credentials"
	],
	"serviceProviderId": "<service provider id>",
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"secretExp": <secret expiration unix epoch>
}
```

| Dato                             | Descrizione                                                                                                                   |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | 
| `<client id>`                    | Indica l’applicazione abilitata a richiedere gli access token.                                                                |
| `<subject id>`                   | ID univoco per mil-auth assegnato al Service Provider che gestisce i POS.                                                     |
| `<salt>`                         | Salt da adoperare per calcolare l’hash del client secret assegnato.                                                           |
| `<secret hash>`                  | Hash del client secret assegnato.                                                                                             |
| `<secret expiration unix epoch>` | Scadenza del client secret assegnato, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |

### Ente
```json
{
	"id": "<client id>",
	"type": "PUBLIC_ADMINISTRATION",
	"grantTypes": [
		"client_credentials"
	],
	"payeeCode": "<payee code>",
	"salt": "<salt>",
	"secretHash": "<secret hash>",
	"secretExp": <secret expiration unix epoch>
}
```

| Dato                             | Descrizione                                                                                                                   |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | 
| `<client id>`                    | Indica l’applicazione abilitata a richiedere gli access token.                                                                |
| `<payee code>`                   | Codice fiscale dell’Ente.                                                                                                     |
| `<salt>`                         | Salt da adoperare per calcolare l’hash del client secret assegnato.                                                           |
| `<secret hash>`                  | Hash del client secret assegnato.                                                                                             |
| `<secret expiration unix epoch>` | Scadenza del client secret assegnato, espresso in numero di secondi dalla mezzanotte del 1° gennaio 1970 nel fuso orario UTC. |

### ATM Layer
```json
{
	"id": "<client id>",
	"type": "ATM",
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

### mil-auth
TODO

### mil-payment-notice
TODO

## Gestione dei ruoli

### POS
In questo caso il client descriptor contiene l’attributo `type` valorizzato con `POS`.

I ruoli sono indicati dall’anagrafica dei terminali (mil-terminal-registry).

I possibili valori del claim `groups` presente nel payload dell'access token sono:
* `NoticePayer`
* `PayWithIDPay`

### Gestore POS
In questo caso il client descriptor contiene l’attributo `type` valorizzato con `POS_SERVICE_PROVIDER`.

I ruoli sono indicati nel file `pos_service_providers/<service provider id>.json`

WORKINPROGRESS

### Ente
In questo caso il client descriptor contiene l’attributo `payeeCode`.

I ruoli sono indicati nel file `publicadministrations/<payee code>.json`

WORKINPROGRESS

### ATM Layer
In questo caso il client descriptor contiene l’attributo `type` valorizzato con `ATM`.

WORKINPROGRESS

### mil-auth
TODO

### mil-payment-notice
TODO