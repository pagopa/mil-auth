# ------------------------------------------------------------------------------
# Container app.
# ------------------------------------------------------------------------------
resource "azurerm_container_app" "auth" {
  name                         = "${local.project}-auth-ca"
  container_app_environment_id = data.azurerm_container_app_environment.mil.id
  resource_group_name          = data.azurerm_container_app_environment.mil.resource_group_name
  revision_mode                = "Single"

  template {
    container {
      name   = "mil-auth"
      image  = var.mil_auth_image
      cpu    = var.mil_auth_cpu
      memory = var.mil_auth_memory

      env {
        name  = "TZ"
        value = "Europe/Rome"
      }

      env {
        name  = "auth.quarkus-log-level"
        value = var.mil_auth_quarkus_log_level
      }

      env {
        name  = "auth.quarkus-rest-client-logging-scope"
        value = var.mil_auth_quarkus_rest_client_logging_scope
      }

      env {
        name  = "auth.app-log-level"
        value = var.mil_auth_app_log_level
      }

      env {
        name  = "auth.cryptoperiod"
        value = var.mil_auth_cryptoperiod
      }

      env {
        name  = "auth.keysize"
        value = var.mil_auth_keysize
      }

      env {
        name  = "auth.access.duration"
        value = var.mil_auth_access_duration
      }

      env {
        name  = "auth.refresh.duration"
        value = var.mil_auth_refresh_duration
      }

      env {
        name        = "auth.data.url"
        secret_name = "storage-account-auth-primary-blob-endpoint"
      }

      env {
        name        = "auth.keyvault.url"
        secret_name = "key-vault-auth-vault-uri"
      }

      env {
        name  = "auth.base-url"
        value = var.mil_auth_base_url
      }

      env {
        name        = "application-insights.connection-string"
        secret_name = "application-insigths-mil-connection-string"
      }

      env {
        name  = "auth.json-log"
        value = var.mil_auth_json_log
      }

      env {
        name  = "auth.keyvault.maxresults"
        value = var.mil_auth_keyvault_maxresults
      }

      env {
        name  = "auth.keyvault.backoff.number-of-attempts"
        value = var.mil_auth_keyvault_backoff_num_of_attempts
      }

      env {
        name  = "jwt-publickey-location"
        value = "http://127.0.0.1:8080/.well-known/jwks.json"
      }

      env {
        name  = "mongodb.connect-timeout"
        value = var.mil_auth_mongodb_connect_timeout
      }

      env {
        name  = "mongodb.read-timeout"
        value = var.mil_auth_mongodb_read_timeout
      }

      env {
        name  = "mongodb.server-selection-timeout"
        value = var.mil_auth_mongodb_server_selection_timeout
      }

      env {
        name        = "mongodb.connection-string-1"
        secret_name = "cosmosdb-account-mil-primary-mongodb-connection-string"
      }

      env {
        name        = "mongodb.connection-string-2"
        secret_name = "cosmosdb-account-mil-secondary-mongodb-connection-string"
      }

      env {
        name        = "IDENTITY_CLIENT_ID"
        secret_name = "identity-client-id"
      }
    }

    max_replicas = var.mil_auth_max_replicas
    min_replicas = var.mil_auth_min_replicas
  }

  secret {
    name                = "cosmosdb-account-mil-primary-mongodb-connection-string"
    key_vault_secret_id = "${data.azurerm_key_vault.general.vault_uri}secrets/${var.cosmosdb_account_primary_mongodb_connection_string_kv_secret}"
    identity            = data.azurerm_user_assigned_identity.auth.id
  }

  secret {
    name                = "cosmosdb-account-mil-secondary-mongodb-connection-string"
    key_vault_secret_id = "${data.azurerm_key_vault.general.vault_uri}secrets/${cosmosdb_account_secondary_mongodb_connection_string_kv_secret}"
    identity            = data.azurerm_user_assigned_identity.auth.id
  }

  secret {
    name                = "storage-account-auth-primary-blob-endpoint"
    key_vault_secret_id = "${data.azurerm_key_vault.general.vault_uri}secrets/${storage_account_primary_blob_endpoint_kv_secret}"
    identity            = data.azurerm_user_assigned_identity.auth.id
  }

  secret {
    name                = "key-vault-auth-vault-uri"
    key_vault_secret_id = "${data.azurerm_key_vault.general.vault_uri}secrets/${key_vault_auth_vault_uri_kv_secret}"
    identity            = data.azurerm_user_assigned_identity.auth.id
  }

  secret {
    name                = "application-insigths-mil-connection-string"
    key_vault_secret_id = "${data.azurerm_key_vault.general.vault_uri}secrets/${application_insigths_connection_string_kv_secret}"
    identity            = data.azurerm_user_assigned_identity.auth.id
  }

  secret {
    name  = "identity-client-id"
    value = "${data.azurerm_user_assigned_identity.auth.client_id}"
  }

  identity {
    type = "UserAssigned"
    identity_ids = [data.azurerm_user_assigned_identity.auth.id]
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    transport        = "http"

    traffic_weight {
      latest_revision = true
      percentage      = 100
      #revision_suffix = formatdate("YYYYMMDDhhmmssZZZZ", timestamp())
    }
  }

  tags = var.tags
}