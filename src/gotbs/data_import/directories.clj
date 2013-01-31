(ns gotbs.data-import.directories
  (:require [datomic.api :as d]
        [gotbs.data-import.file-importer :as fi])
  (import [java.io File FileOutputStream FileInputStream]
          [java.nio.channels Channels]
          [java.util.zip GZIPOutputStream]))

;; (def uri "datomic:free://localhost:4334/gotbs")

(defn files [dirname]
  (seq (.listFiles (File. dirname))))

(defn process-files [srcdir destdir f]
  (let [file-filter
        (proxy [java.io.FilenameFilter] []
          (accept [f s] (.startsWith s "vehicles")))]

    (doseq [file  (.listFiles srcdir file-filter)]
      (let [istream (FileInputStream. file)
            ostream (GZIPOutputStream.
                     (FileOutputStream.
                      (File. destdir
                             (str (.getName file) ".gz"))))]
        (do
          (f file)
          (.transferTo
           (.getChannel istream) 0 (.length file) (Channels/newChannel ostream))
          (.close ostream)
          (.close istream)
          (.delete file))))))


(defn -main [uri src dest]
  "run with datomic:dev://localhost:4334/<DB-NAME>"
  (let [conn (d/connect uri)
        srcdir (File. src)
        destdir (File. dest)]
    (process-files
     srcdir
     destdir
     (fn [file]
       (d/transact conn
                   (fi/transactions file (d/db conn))))))
  (System/exit 0))
