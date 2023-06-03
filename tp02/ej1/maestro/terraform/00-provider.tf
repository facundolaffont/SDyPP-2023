
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
  project     = var.project_id
  region      = var.region
  zone        = var.zone
}