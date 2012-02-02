(ns gotbs.run
  (:require [gotbs.bootstrap.webapp :as webapp]))

(defn -main []
  (webapp/start-all)
  (.start (Thread. #())))
