# ------------------------------------------------------------------------------
# Container Apps Environment.
# ------------------------------------------------------------------------------
data "azurerm_container_app_environment" "mil" {
  name                = "${local.project}-cae"
  resource_group_name = "${local.project}-app-rg"
}