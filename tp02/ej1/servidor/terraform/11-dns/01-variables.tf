variable "region" {
  type    = string
  default = "us-central1"
}

variable "zone" {
  type    = string
  default = "us-central1-a"
}

variable "project_id" {
  type    = string
  default = "heroic-night-388500"
}

# Variables desde GitHub.
variable "cf_api_email" {
  description = "Email de la cuenta de Cloudflare"
  type = string
}
variable "cf_api_key" {
  description = "Clave de API de la cuenta de Cloudflare"
  type = string
}
variable "google_credentials" {
  description = "Credenciales de Google Cloud"
  type = string
}

# Obtiene la información de la zona del dominio de Cloudflare.
data "cloudflare_zone" "app" {
  name = "fl.com.ar"
}

# Obtiene el servicio del balanceo de carga.
data "kubernetes_service" "load_balancer" {
  metadata {
    name      = "maestro-service"
    namespace = "default"
  }
}