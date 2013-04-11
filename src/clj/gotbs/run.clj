(ns gotbs.run
  (:gen-class)
  (:require [gotbs.bootstrap.webapp :as webapp]
            [datomic.api :as datomic]))

(def datomic-uri "datomic:free://localhost:4334/gotbs")

(defn -main []
  (webapp/start-all (datomic/connect datomic-uri))
  (.start (Thread. #())))
