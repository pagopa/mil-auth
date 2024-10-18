# ------------------------------------------------------------------------------
# Container Apps Environment.
# ------------------------------------------------------------------------------
data "azurerm_container_app_environment" "mil" {
  name                = "${local.project}-cae"
  resource_group_name = "${local.project}-app-rg"
}

# ------------------------------------------------------------------------------
# Identity for this Container App.
# ------------------------------------------------------------------------------
data "azurerm_user_assigned_identity" "auth" {
  resource_group_name = "${local.project}-identity-rg"
  name                = "${var.prefix}-${var.env_short}-auth-identity"
}

# ------------------------------------------------------------------------------
# General purpose key vault used to protect secrets.
# ------------------------------------------------------------------------------
data "azurerm_key_vault" "general" {
  name                = "${local.project}-general-kv"
  resource_group_name = "${local.project}-sec-rg"
}

# ------------------------------------------------------------------------------
# Key vault for cryptographics operations.
# ------------------------------------------------------------------------------
data "azurerm_key_vault" "auth" {
  name                = "${local.project}-auth-kv"
  resource_group_name = "${local.project}-sec-rg"
}

# ------------------------------------------------------------------------------
# Storage account containing configuration files.
# ------------------------------------------------------------------------------
data "azurerm_storage_account" "auth" {
  name                = "${var.prefix}${var.env_short}authst"
  resource_group_name = "${local.project}-data-rg"
}