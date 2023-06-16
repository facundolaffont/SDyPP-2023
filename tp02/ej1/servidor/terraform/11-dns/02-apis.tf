# Crea el registro DNS que resuelve el nombre app.fl.com.ar
# a la IP del balanceador de cargas.
resource "cloudflare_record" "app_loadbalancer" {
  zone_id = data.cloudflare_zone.app
  name    = "app"
  type    = "A"
  value   = var.loadbalancer_ip
  ttl     = 300
  proxied = true
}