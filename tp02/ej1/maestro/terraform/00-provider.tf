
terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">=4.60.0"
    }
  }
  backend "gcs" {
  }


  required_version = ">= 1.4.5"
}


provider "google" {
  #credentials = file(var.credentials_file_path)
  project     = var.project_id
  region      = var.region
  zone        = var.zone
}