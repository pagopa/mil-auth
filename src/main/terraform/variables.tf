# ------------------------------------------------------------------------------
# Generic variables definition.
# ------------------------------------------------------------------------------
variable "prefix" {
  type = string
  validation {
    condition = (
      length(var.prefix) <= 6
    )
    error_message = "Max length is 6 chars."
  }
}

variable "env" {
  type = string
  validation {
    condition = (
      length(var.env) <= 4
    )
    error_message = "Max length is 4 chars."
  }
}

variable "env_short" {
  type = string
  validation {
    condition = (
      length(var.env_short) <= 1
    )
    error_message = "Max length is 1 chars."
  }
}

variable "location" {
  type    = string
  default = "westeurope"
}

variable "location_short" {
  type        = string
  description = "Location short like eg: neu, weu."
}

variable "tags" {
  type = map(any)
  default = {
    CreatedBy = "Terraform"
  }
}

# ------------------------------------------------------------------------------
# Specific variables definition.
# ------------------------------------------------------------------------------
variable "mil_auth_quarkus_log_level" {
  type    = string
  default = "ERROR"
}

variable "mil_auth_app_log_level" {
  type    = string
  default = "DEBUG"
}

variable "mil_auth_json_log" {
  type    = bool
  default = true
}

variable "mil_auth_quarkus_rest_client_logging_scope" {
  description = "Scope for Quarkus REST client logging. Allowed values are: all, request-response, none."
  type        = string
  default     = "all"
}

variable "mil_auth_cryptoperiod" {
  type    = number
  default = 86400000
}

variable "mil_auth_keysize" {
  type    = number
  default = 4096
}

variable "mil_auth_access_duration" {
  type    = number
  default = 900
}

variable "mil_auth_refresh_duration" {
  type    = number
  default = 3600
}

variable "mil_auth_openapi_descriptor" {
  type = string
}

variable "mil_auth_image" {
  type = string
}

variable "mil_auth_cpu" {
  type    = number
  default = 1
}

variable "mil_auth_memory" {
  type    = string
  default = "2Gi"
}

variable "mil_auth_max_replicas" {
  type    = number
  default = 10
}

variable "mil_auth_min_replicas" {
  type    = number
  default = 1
}

variable "mil_auth_keyvault_maxresults" {
  type    = number
  default = 20
}

variable "mil_auth_keyvault_backoff_num_of_attempts" {
  type    = number
  default = 3
}

variable "mil_auth_mongodb_connect_timeout" {
  type    = string
  default = "5s"
}

variable "mil_auth_mongodb_read_timeout" {
  type    = string
  default = "10s"
}

variable "mil_auth_mongodb_server_selection_timeout" {
  type    = string
  default = "5s"
}

variable "mil_auth_base_url" {
  type = string
}