# ------------------------------------------------------------------------------
# General variables.
# ------------------------------------------------------------------------------
prefix         = "cstar"
env_short      = "d"
env            = "dev"
location       = "italynorth"
location_short = "itn"
domain         = "mcshared"

# ------------------------------------------------------------------------------
# External resources.
# ------------------------------------------------------------------------------
cae_name                       = "cstar-d-itn-mcshared-cae"
cae_resource_group_name        = "cstar-d-itn-mcshared-compute-rg"
id_name                        = "cstar-d-itn-mcshared-auth-id"
id_resource_group_name         = "cstar-d-itn-mcshared-security-rg"
general_kv_name                = "cstar-d-itn-mc-gen-kv"
general_kv_resource_group_name = "cstar-d-itn-mcshared-security-rg"
auth_kv_name                   = "cstar-d-itn-mc-auth-kv"
auth_kv_resource_group_name    = "cstar-d-itn-mcshared-security-rg"

# ------------------------------------------------------------------------------
# Names of key vault secrets.
# ------------------------------------------------------------------------------
cosmosdb_account_primary_mongodb_connection_string_kv_secret   = "cosmosdb-account-mcshared-primary-mongodb-connection-string"
cosmosdb_account_secondary_mongodb_connection_string_kv_secret = "cosmosdb-account-mcshared-secondary-mongodb-connection-string"
key_vault_auth_vault_uri_kv_secret                             = "key-vault-auth-vault-uri"
application_insigths_connection_string_kv_secret               = "core-application-insigths-connection-string"

# ------------------------------------------------------------------------------
# Configuration of the microservice.
# ------------------------------------------------------------------------------
mil_auth_quarkus_log_level                 = "DEBUG"
mil_auth_app_log_level                     = "DEBUG"
mil_auth_json_log                          = false
mil_auth_quarkus_rest_client_logging_scope = "all"
mil_auth_cryptoperiod                      = 31536000
mil_auth_keysize                           = 4096
mil_auth_access_duration                   = 1800
mil_auth_refresh_duration                  = 21600
mil_auth_image                             = "ghcr.io/pagopa/mil-auth:latest"
mil_auth_cpu                               = "0.25"
mil_auth_memory                            = "0.5Gi"
mil_auth_max_replicas                      = 5
mil_auth_min_replicas                      = 0
mil_auth_keyvault_maxresults               = 20
mil_auth_keyvault_backoff_num_of_attempts  = 5
mil_auth_mongodb_connect_timeout           = "5s"
mil_auth_mongodb_read_timeout              = "10s"
mil_auth_mongodb_server_selection_timeout  = "5s"
mil_auth_base_url                          = "https://api-mcshared.dev.cstar.pagopa.it/auth"

# ------------------------------------------------------------------------------
# Configuration of the Container App used by the microservice.
# ------------------------------------------------------------------------------
workload_profile_name = "Consumption"
