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