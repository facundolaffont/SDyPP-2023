variable "region" {
  type    = string
  default = "us-central1"
}

variable "zone" {
  type    = string
  default = "us-central1-a"
}

// Se quita cuando se utiliza el pipeline.
variable "credentials_file_path" {
  description = "Ruta del archivo de credenciales para la cuenta de servicios de GCP"
  default     = "./terraform.json"
}

variable "project_id" {
  type    = string
  default = "heroic-night-388500"
}