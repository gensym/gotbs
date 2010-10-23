(ns gotbs.web.locations
  (:use
   net.cgrand.enlive-html
   [gotbs.bustracker :only (stop-name north-bound west-bound)]
   [gotbs.stopinfo :only (make-stop-arrival-info)]))

(def *dummy-context*
     [{
       :stop-name "North/Milwaukee/Damen"
       :route "72 North"
       :direction "Westbound"
       :eta "4:20"
       }
      {
       :stop-name "North/Milwaukee/Damen"
       :route "50 Armitage"
       :direction "Westbound"
       :eta "4:25"
       }])

(defsnippet locations-model "web/locations.html" 
  [:#locations [:.location (nth-of-type 1)]]
  [{:keys [stop-name route direction eta]}]
  [:.stop-name] (content stop-name)
  [:.route-name] (content route)
  [:.route-direction] (content direction)
  [:.prediction-time] (content eta))

(def north-route "72")

(def armitage-route "73")
(def damen-milwaukee-western "945")
(def milwaukee-and-armitage "14564")
(def western-and-armitage "4105")

(defn stops-with-arrival-info []
  (map 
   (fn [[route direction stop]]
     (map
      locations-model
      (make-stop-arrival-info route direction stop)))
   (list (list north-route west-bound damen-milwaukee-western) (list armitage-route west-bound western-and-armitage))))

(defsnippet locations-content "web/locations.html"
  [:div#locations]
  [req]
  [:div#locations] (content (stops-with-arrival-info)))

(deftemplate pagetemplate "web/index.html"
  [req]
  [:div#main] (content (locations-content req)))

(defn locations [req]
     (pagetemplate req))