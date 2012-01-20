(ns gotbs.web.route-subscriber
  (:use [clojure.contrib.logging :as log])
  (:use [gotbs.route-data :only (route-descriptor)]))

(defn location-subscriber [[route-display-name direction]]
  (let [route (:rt (route-descriptor route-display-name))]
    (log/info (str "!!! - Subscribed to " route " - " direction))))


