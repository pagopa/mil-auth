locals {
  #
  # Project label.
  #
  project = var.domain == "" ? "${var.prefix}-${var.env_short}" : "${var.prefix}-${var.env_short}-${var.domain}"
  
  #
  # Resources tags.
  #
  tags = {
    CreatedBy   = "Terraform"
    Environment = var.env_short
    Owner       = var.prefix
    Source      = "https://github.com/pagopa/mil-auth/tree/main/src/main/terraform"
    CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
    Application = var.domain == "" ? var.prefix : var.domain
  }
}