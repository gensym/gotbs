(ns gotbs.web.locations
  (:use
   net.cgrand.enlive-html
   [gotbs.bustracker :only (stop-name north-bound west-bound)]
   [gotbs.stopinfo :only (make-stop-arrival-info)]))

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

(defn stops-with-arrival-info [routes]
  (map 
   (fn [{route :route direction :direction stop :stop}]
     (map
      locations-model
      (make-stop-arrival-info route direction stop)))
   routes))

(defsnippet locations-content "web/locations.html"
  [:div#locations]
  [req]
  [:div#locations] (content
                    (stops-with-arrival-info
                      (list
                       {:route north-route :direction west-bound :stop damen-milwaukee-western}
                       {:route armitage-route :direction west-bound :stop western-and-armitage}))))

(deftemplate pagetemplate "web/index.html"
  [req]
  [:div#main] (content (locations-content req)))

(defn locations [req]
     (pagetemplate req))
