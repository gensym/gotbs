(ns gotbs.data-import.import-troubleshoot
  (:require [datomic.api :as d]
            [gotbs.data-import.file-importer :as fi]
            [gotbs.util.relational :as rel])
  (import [java.io File FileOutputStream FileInputStream]
          [java.nio.channels Channels]
          [java.util.zip GZIPOutputStream]))

(def uri "datomic:free://localhost:4334/gotbs")

(def src (File. "/Users/daltenburg/dev/busdata.test/source/vehicles-120308024401.xml"))


(comment (def conn (d/connect uri)))

(comment (def transactions (fi/transactions src (d/db conn))))
(comment (def trouble (fi/trouble src (d/db conn))))

(comment (def truns (map (partial fi/find-run (d/db conn)) trouble)))

(comment (d/transact conn transactions))



;; Note - we're missing runpoints, but we have a lot of duplicate shit
