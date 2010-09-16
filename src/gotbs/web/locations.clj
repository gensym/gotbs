(ns gotbs.web.locations
  (:use
   net.cgrand.enlive-html
   [gotbs.bustracker :only (stop-name)]
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

;; TODO - this snippet whacks out the wrapping "locations" div. Probably not what we want
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
(def western-and-armitage "4105")

(defn stops-with-arrival-info []
  (map 
   (fn [[stop route]]
     (map
      locations-model
      (make-stop-arrival-info stop route)))
   (list (list damen-milwaukee-western north-route) (list western-and-armitage armitage-route))))

(deftemplate locations "web/index.html"
  [req] ; Unused right now
  [:div#main] (content (stops-with-arrival-info)))