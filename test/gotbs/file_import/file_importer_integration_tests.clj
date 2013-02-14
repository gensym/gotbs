(ns gotbs.file-import.file-importer-integration-tests
  (require [datomic.api :as d]
           [gotbs.data-import.create-db :as create]
           [gotbs.data-import.file-importer :as fi]
           [gotbs.data-import.query :as q])
  (import [java.io File]))

(comment (def uri  "datomic:free://localhost:4334/gotbs-test"))
(comment (def conn (d/connect uri)))

(defn test-db-import [uri]
  (create/create-db uri)
  (let [datadir (File. 
                 (.toURI
                  (ClassLoader/getSystemResource "test-data/vehicle-imports")))
        file-filter (proxy [java.io.FilenameFilter] []
                      (accept [f s] (.startsWith s "vehicles")))
        conn (d/connect uri)]
    (try
      (doseq [file (.listFiles datadir file-filter)]
        (d/transact conn
                    (fi/transactions file (d/db conn))))

      (println "Checking runs...")
      (let [runs (q/runs conn)]
        (clojure.test/is (= 6 (count runs)))
        (println "OK"))
      (finally
        (do (println "Cleaning up")
            (d/delete-database uri)))))

  (defn -main [uri]
    (org.apache.log4j.BasicConfigurator/configure)
    (test-db-import uri)))

(comment (test-db-import "datomic:free://localhost:4334/gotbs-test"))
