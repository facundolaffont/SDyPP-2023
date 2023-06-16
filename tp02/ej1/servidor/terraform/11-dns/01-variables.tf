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

# Obtiene el servicio del balanceo de carga.
data "kubernetes_service" "load_balancer" {
  metadata {
    name      = "maestro-service"
    namespace = "default"
  }
}