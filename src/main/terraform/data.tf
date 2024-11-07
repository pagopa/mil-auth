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