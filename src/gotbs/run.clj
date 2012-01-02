(ns gotbs.run
  (use
   [gotbs.websockets.jetty]
   [gotbs.websockets.webbit])
  (require [gotbs.core]
           [clojure.contrib.json :as json]
           [gotbs.util.scheduler :as scheduler]
           [gotbs.vehcles-on-routes :as routes]
           [gotbs.websockets.connections :as ws-connections]))

(defn -main []
  (let [connection-set (ws-connections/connection-set)
        routes-to-publish (routes/make-routes-to-publish)]

    (scheduler/periodically
     (fn [] (ws-connections/broadcast connection-set "foo" (json/json-str {:type "broadcasted" :message "testing..."})))
     1000
     1000)

    (scheduler/periodically
     (fn [] (process-routes-queue routes-to-publish))
     60000
     60000)
    
    (run-webbit-websockets 8888 connection-set)
    (run-jetty-websockets #'gotbs.core/app 8080)))


  
