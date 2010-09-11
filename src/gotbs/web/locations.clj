(ns gotbs.web.locations
  (:use
   net.cgrand.enlive-html
   gotbs.bustracker (make-predictions)))

					;(defn do-get [req]
					;  (str "Hello world: " (:uri req) " " (:query-string req)))

(def *dummy-context*
     [{
       :route "72 North"
       :direction "Westbound"
       :eta "4:20"
       }
      {
       :route "50 Armitage"
       :direction "Westbound"
       :eta "4:25"
       }])

(defsnippet locations-model "web/locations.html" 
  [:#locations [:.location (nth-of-type 1)]]
  [{:keys [route direction eta]}]
  [:.route-name] (content route)
  [:.route-direction] (content direction)
  [:.prediction-time] (content eta))

(deftemplate locations "web/index.html"
  [req] ; Unused right now
  [:div#main] (content (map locations-model (make-predictions))))