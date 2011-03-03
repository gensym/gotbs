(ns gotbs.web.routes
  (:use net.cgrand.enlive-html))

(defsnippet routes-content "web/routes.html"
  [:div#routes]
  [req]
  [:script#data] (content "var data = 
    [{ lat: 41.882114939198, lon: -87.625998258591 },
     { lat: 41.882039054742, lon: -87.629334926605 },
     { lat: 41.882019085133, lon: -87.630681395531 } ];"))


(deftemplate routestemplate "web/index.html"
  [req]
  [:div#main] (content (routes-content req)))

(defn routes [req]
  (routestemplate req))

    
