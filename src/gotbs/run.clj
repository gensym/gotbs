(ns gotbs.run
  (use
   [gotbs.websockets.jetty]
   [gotbs.websockets.webbit])
  (require [gotbs.core]
           [clojure.contrib.json :as json]
           [gotbs.util.scheduler :as scheduler]
           [gotbs.busdata :as busdata]
           [gotbs.util :as util]
           [gotbs.util.pull-queue :as pull-queue]
           [gotbs.websockets.connections :as ws-connections]))

(defn- make-routes-queue [connection-set]
  (let [fetcher (util/wrap-with-input #(busdata/in-flight-vehicles [%]))
        publisher (fn [[route vehicles]]
                    (ws-connections/broadcast connection-set route (json/json-str vehicles)))]
    (pull-queue/make-pull-queue fetcher publisher 1)))

(defn -main []
  (let [connection-set (ws-connections/connection-set)
        routes-to-publish (make-routes-queue connection-set)]

    (scheduler/periodically
     (fn [] (ws-connections/broadcast connection-set "foo" (json/json-str {:type "broadcasted" :message "testing..."})))
     1000
     1000)

    (scheduler/periodically
     #(pull-queue/process-all-items routes-to-publish)
     60000
     60000)
    
    (run-webbit-websockets 8888 connection-set)
    (run-jetty-websockets #'gotbs.core/app 8080)))


  
