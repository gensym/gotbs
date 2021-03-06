(ns gotbs.data-import.create-db
  (:use [datomic.api :only [db q] :as d]))

(defn create-db [uri]
  (d/create-database uri)
  (let [conn (d/connect uri)
        import-schema (read-string  (slurp "resources/schema/location-schema.dtm"))
        run-schema (read-string (slurp "resources/schema/run-schema.dtm"))
        ]
    @(d/transact conn import-schema)
    @(d/transact conn run-schema)))

(defn -main [uri]
  "run with datomic:dev://localhost:4334/<DB-NAME>"
  (org.apache.log4j.BasicConfigurator/configure)
  (create-db uri)
  (System/exit 0))
