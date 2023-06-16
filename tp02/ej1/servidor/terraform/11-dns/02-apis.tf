# Crea el registro DNS que resuelve el nombre app.fl.com.ar
# a la IP del balanceador de cargas.
resource "cloudflare_record" "app_loadbalancer" {
  zone_id = data.cloudflare_zone.app
  name    = "app"
  type    = "A"
  value   = data.kubernetes_service.load_balancer.status[0].load_balancer[0].ingress[0].ip
  ttl     = 300
  proxied = true
}