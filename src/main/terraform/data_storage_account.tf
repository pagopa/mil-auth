# ------------------------------------------------------------------------------
# Storage account containing configuration files.
# ------------------------------------------------------------------------------
data "azurerm_storage_account" "auth" {
  name                = "${var.prefix}${var.env_short}authst"
  resource_group_name = "${local.project}-data-rg"
}