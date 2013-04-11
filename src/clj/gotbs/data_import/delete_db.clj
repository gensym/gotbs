(ns gotbs.data-import.delete-db
  (:use [datomic.api :only [db q] :as d]))

(defn -main [uri]
  "run with datomic:dev://localhost:4334/<DB-NAME>"
  (org.apache.log4j.BasicConfigurator/configure)
  (d/delete-database uri)
  (System/exit 0))
