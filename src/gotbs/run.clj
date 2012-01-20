(ns gotbs.run
  (use
   [gotbs.websockets.jetty]
   [gotbs.websockets.webbit]
   [clojure.contrib [logging :as log]])
  (require [gotbs.core]
           [clojure.contrib.json :as json]
           [gotbs.util.scheduler :as scheduler]
           [gotbs.busdata :as busdata]
           [gotbs.util :as util]
           [gotbs.util.pull-queue :as pull-queue]
           [gotbs.websockets.connections :as ws-connections]
           [gotbs.websockets.connection-subscriber :as ws-subscriber]))


(defn subscriber [topic]
  (log/info (str "Subscribed to " topic)))

(def connection-set (ws-connections/connection-set))

(defn publisher  [[route vehicles]]
  (ws-connections/broadcast connection-set route (json/json-str vehicles)))

(def routes-to-publish
  (let [fetcher (util/wrap-with-input #(busdata/in-flight-vehicles [%]))]
    (pull-queue/make-pull-queue fetcher publisher 1)))

(def connection-subscriber
     (ws-subscriber/connection-subscriber
      connection-set
      subscriber))

(defn -main []
  (scheduler/periodically
   (fn [] (ws-connections/broadcast connection-set "foo" (json/json-str {:type "broadcasted" :message "testing..."})))
   1000
   1000)

  (scheduler/periodically
   #(pull-queue/process-all-items routes-to-publish)
   60000
   60000)
    
  (run-webbit-websockets 8888 connection-subscriber)
  (run-jetty-server #'gotbs.core/app 8080))
