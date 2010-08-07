(ns gotbs.bustracker
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader)))

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
	(apply str (line-seq buf))))))

(defn fetch-location-data []
  (fetch-url 
   "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=kv6yHNkUrkZJkjA8u7V5sxNTq&rt=72,50"))

