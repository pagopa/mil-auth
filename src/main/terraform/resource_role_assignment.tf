# ------------------------------------------------------------------------------
# Assignement of role "Key Vault Crypto Officer" to system-managed identity of
# container app, to use key vault.
# ------------------------------------------------------------------------------
resource "azurerm_role_assignment" "auth_kv" {
  scope                = data.azurerm_key_vault.auth.id
  role_definition_name = "Key Vault Crypto Officer"
  principal_id         = azurerm_container_app.auth.identity[0].principal_id
}

# ------------------------------------------------------------------------------
# Assignement of role "Key Vault Secrets Officer" to system-managed identity of
# container app, to use key vault.
# ------------------------------------------------------------------------------
resource "azurerm_role_assignment" "general_kv_to_read_secrets" {
  scope                = data.azurerm_key_vault.general.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_container_app.auth.identity[0].principal_id
}

# ------------------------------------------------------------------------------
# Assignement of role "Storage Blob Data Reader" to system-managed identity of
# container app, to use storage account.
# ------------------------------------------------------------------------------
resource "azurerm_role_assignment" "auth_storage" {
  scope                = data.azurerm_storage_account.auth.id
  role_definition_name = "Storage Blob Data Reader"
  principal_id         = azurerm_container_app.auth.identity[0].principal_id
}