#
# General
#
env_short      = "d"
env            = "dev"
prefix         = "mil"
location       = "westeurope" # this will be "italynorth"
location_short = "weu"        # this will be "itn"

tags = {
  CreatedBy   = "Terraform"
  Environment = "DEV"
  Owner       = "mil"
  Source      = "https://github.com/pagopa/mil-infra"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

#
# mil-auth
#
mil_auth_quarkus_log_level                 = "ERROR"
mil_auth_app_log_level                     = "DEBUG"
mil_auth_json_log                          = true
mil_auth_quarkus_rest_client_logging_scope = "all"
mil_auth_cryptoperiod                      = 43200
mil_auth_keysize                           = 2048
mil_auth_access_duration                   = 900
mil_auth_refresh_duration                  = 3600
mil_auth_openapi_descriptor                = "https://raw.githubusercontent.com/pagopa/mil-auth/main/src/main/resources/META-INF/openapi.yaml"
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