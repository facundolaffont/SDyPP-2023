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

# Define el proveedor de Kubernetes para poder obtener el servicio que balancea cargas, y asì
# obtener su IP pública, para asignarla a los servidores de nombre de Cloudflare.
provider "kubernetes" {
  cluster_ca_certificate = var.google_credentials
}

# Define el proveedor de Cloudflare para crear los registros que permitirán resolver el nombre de dominio,
# devolviendo la IP pública del balanceador de cargas.
# provider "cloudflare" {
#   api_token = var.cf_api_key
# }