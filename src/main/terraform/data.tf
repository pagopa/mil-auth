# ------------------------------------------------------------------------------
# Container Apps Environment.
# ------------------------------------------------------------------------------
data "azurerm_container_app_environment" "mil" {
  name                = var.cae_name
  resource_group_name = var.cae_resource_group_name
}

# ------------------------------------------------------------------------------
# Identity for this Container App.
# ------------------------------------------------------------------------------
data "azurerm_user_assigned_identity" "auth" {
  name                = var.id_name
  resource_group_name = var.id_resource_group_name
}

# ------------------------------------------------------------------------------
# General purpose key vault used to protect secrets.
# ------------------------------------------------------------------------------
data "azurerm_key_vault" "general" {
  name                = var.general_kv_name
  resource_group_name = var.general_kv_resource_group_name
}

# ------------------------------------------------------------------------------
# Key vault for cryptographics operations.
# ------------------------------------------------------------------------------
data "azurerm_key_vault" "auth" {
  name                = var.auth_kv_name
  resource_group_name = var.auth_kv_resource_group_name
}

# ------------------------------------------------------------------------------
# Storage account containing configuration files.
# ------------------------------------------------------------------------------
data "azurerm_storage_account" "auth" {
  name                = var.auth_st_name
  resource_group_name = var.auth_st_resource_group_name
}