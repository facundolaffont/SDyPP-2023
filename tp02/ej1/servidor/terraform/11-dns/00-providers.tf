# https://developer.hashicorp.com/terraform/language/settings
terraform {
  
  # https://developer.hashicorp.com/terraform/language/settings#specifying-provider-requirements
  required_providers {

    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }

    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.21.1"
    }

    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = ">= 4.8.0"
    }

  }

  # https://developer.hashicorp.com/terraform/language/settings/backends/configuration
  # Crea la carpeta .terraform y sus archivos.
  backend "gcs" {}

  # https://developer.hashicorp.com/terraform/language/settings#specifying-a-required-terraform-version
  required_version = ">= 1.4.5"
}

# https://developer.hashicorp.com/terraform/language/providers/configuration
provider "google" {
  project     = var.project_id
  region      = var.region
  zone        = var.zone
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "my-context"
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}