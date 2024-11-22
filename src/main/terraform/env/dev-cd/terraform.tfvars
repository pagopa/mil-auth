# ------------------------------------------------------------------------------
# General variables.
# ------------------------------------------------------------------------------
prefix         = "mil"
env_short      = "d"
env            = "dev"
location       = "westeurope" # this will be "italynorth"
location_short = "weu"        # this will be "itn"

tags = {
  CreatedBy   = "Terraform"
  Environment = "DEV"
  Owner       = "mil"
  Source      = "https://github.com/pagopa/mil-auth/tree/main/src/main/terraform"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

# ------------------------------------------------------------------------------
# External resources.
# ------------------------------------------------------------------------------
cae_name                       = "mil-d-cae"
cae_resource_group_name        = "mil-d-app-rg"
id_name                        = "mil-d-auth-identity"
id_resource_group_name         = "mil-d-identity-rg"
general_kv_name                = "mil-d-general-kv"
general_kv_resource_group_name = "mil-d-sec-rg"
auth_kv_name                   = "mil-d-auth-kv"
auth_kv_resource_group_name    = "mil-d-sec-rg"
auth_st_name                   = "mildauthst"
auth_st_resource_group_name    = "mil-d-data-rg"

# ------------------------------------------------------------------------------
# Names of key vault secrets.
# ------------------------------------------------------------------------------
cosmosdb_account_primary_mongodb_connection_string_kv_secret   = "cosmosdb-account-mil-primary-mongodb-connection-string"
cosmosdb_account_secondary_mongodb_connection_string_kv_secret = "cosmosdb-account-mil-secondary-mongodb-connection-string"
key_vault_auth_vault_uri_kv_secret                             = "key-vault-auth-vault-uri"
application_insigths_connection_string_kv_secret               = "application-insigths-mil-connection-string"

# ------------------------------------------------------------------------------
# Configuration of the microservice.
# ------------------------------------------------------------------------------
mil_auth_quarkus_log_level                 = "ERROR"
mil_auth_app_log_level                     = "DEBUG"
mil_auth_json_log                          = true
mil_auth_quarkus_rest_client_logging_scope = "all"
mil_auth_cryptoperiod                      = 43200
mil_auth_keysize                           = 2048
mil_auth_access_duration                   = 900
mil_auth_refresh_duration                  = 3600
mil_auth_image                             = "ghcr.io/pagopa/mil-auth:latest"
mil_auth_cpu                               = 1
mil_auth_memory                            = "2Gi"
mil_auth_max_replicas                      = 5
mil_auth_min_replicas                      = 1
mil_auth_keyvault_maxresults               = 20
mil_auth_keyvault_backoff_num_of_attempts  = 5
mil_auth_mongodb_connect_timeout           = "5s"
mil_auth_mongodb_read_timeout              = "10s"
mil_auth_mongodb_server_selection_timeout  = "5s"
mil_auth_base_url                          = "https://mil-d-apim.azure-api.net/mil-auth"