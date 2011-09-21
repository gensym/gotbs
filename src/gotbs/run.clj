(ns gotbs.run
  (use
   [gotbs.websockets.jetty]
   [gotbs.websockets.webbit])
  (require [gotbs.core]
           [clojure.contrib.json :as json]
           [gotbs.util.scheduler :as scheduler]
           [gotbs.websockets.connections :as ws-connections]))

(let [connection-set (ws-connections/connection-set)]
  (scheduler/periodically
   (fn [] (ws-connections/broadcast connection-set "foo" (json/json-str {:type "broadcasted" :message "testing..."})))
   1000
   1000)
  (run-webbit-websockets 8888 connection-set)
  (run-jetty-websockets #'gotbs.core/app 8080))


  
