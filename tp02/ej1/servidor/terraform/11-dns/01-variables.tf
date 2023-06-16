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

variable "LOADBALANCER_IP" {
  type = string
}
variable "CLOUDFLARE_EMAIL" {
  type = string
}
variable "CLOUDFLARE_API_KEY" {
  type = string
}
variable "GOOGLE_CREDENTIALS" {
  type = string
}

# Obtiene la información de la zona del dominio de Cloudflare.
data "cloudflare_zone" "app" {
  name = "fl.com.ar"
}

# Obtiene el servicio del balanceo de carga.
# data "kubernetes_service" "load_balancer" {
#   metadata {
#     name      = "maestro-service"
#     namespace = "default"
#   }
# }