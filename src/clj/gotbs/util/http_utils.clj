(ns gotbs.util.http-utils
  (:require [clojure.tools.logging :as log])
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream)))

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (log/info (str "Requesting " url))
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (apply str (line-seq buf))))))
