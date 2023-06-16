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
  config_path    = "~/.kube/config"
  config_context = "my-context"
}
provider "kubernetes" {
  # Optional: Specify the Kubernetes cluster context to use
  # If not provided, it will use the current context configured in your kubeconfig file
  # config_context_override = "my-kubernetes-context"

  # Optional: Specify the path to your kubeconfig file
  # If not provided, it will default to the standard kubeconfig file location
  # config_path = "~/.kube/config"

  # Optional: Specify the version of the Kubernetes API to use
  # If not provided, it will default to the latest stable version
  version = "~> 1.21"

  # Optional: Configure authentication using a service account
  # If not provided, it will use the credentials configured in your kubeconfig file
  # Uncomment the following lines and set the path to your service account key file
  load_config_file = false
  token_credentials {
    token_file = var.google_credentials
  }

  # Optional: Configure authentication using a kubeconfig file entry
  # If not provided, it will use the credentials configured in your kubeconfig file
  # Uncomment the following lines and set the context name
  # load_config_file = true
  # context = "my-kubeconfig-context"
}


# Define el proveedor de Cloudflare para crear los registros que permitirán resolver el nombre de dominio,
# devolviendo la IP pública del balanceador de cargas.
# provider "cloudflare" {
#   api_token = var.cf_api_key
# }