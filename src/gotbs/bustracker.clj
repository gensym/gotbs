(ns gotbs.bustracker
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream))
  (:use
   clojure.xml (parse)
   clojure.java.io (input-stream)))

;; BEGIN HARDCODING
(def api-key "kv6yHNkUrkZJkjA8u7V5sxNTq")

(def direction "West+Bound")

(def destination "Jefferson Park Blue Line")
;; END HARDCODING

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
	(apply str (line-seq buf))))))

(defn fetch-location-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&rt=" route)))

(defn fetch-prediction-data-xml [route stop-id]
  (fetch-url
   (str
    "http://www.ctabustracker.com/bustime/api/v1/getpredictions?key=" api-key
    "&rt=" route
    "&stpid=" stop-id)))

(defn fetch-stop-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getstops?key=" api-key "&rt=" route "&dir=" direction)))

(defn fetch-vehicle-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&rt=" route)))

(defn fetch-pattern-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getpatterns?key=" api-key "&rt=" route)))

(defn content-xml-to-map [xml]
  (let [
	xml-to-map
	(fn [coll tag-attrs]
	  (assoc 
	      coll 
	    (:tag tag-attrs) 
	    (let [content (:content tag-attrs)]
	      (if (> (count content) 1)
		content
		(first content)))))]
    (reduce xml-to-map {} xml)))


(defn filter-tag [tag-name s]
  (filter #(= (:tag %) tag-name) s))

(defn fetch-stop-data [route]
  (map
   content-xml-to-map
   (map
    :content 
    (filter-tag
     :stop
     (:content
      (parse
       (StringBufferInputStream.
	(fetch-stop-data-xml route))))))))

(defn stop-id [route stop-name]
  (first (map :stpid (filter #(= (:stpnm %1) stop-name) (fetch-stop-data route)))))

(defn stop-name [route stop-id]
     (first (map :stpnm (filter #(= (:stpid %) stop-id) (fetch-stop-data route)))))


(defn fetch-vehicle-data [route]
  (map
   :content
   (filter-tag
    :vehicle
    (:content
     (parse (StringBufferInputStream. (fetch-vehicle-data-xml route)))))))

(defn fetch-pattern-data [route dir]
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :pt
     (first
      (filter
       #(= dir (first (:content (first (filter-tag :rtdir %)))))
       (map
	:content
	(filter-tag
	 :ptr
	 (:content
	  (parse (StringBufferInputStream. (fetch-pattern-data-xml route))))))))))))

(defn fetch-prediction-data [route stop-id]
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :prd
     (:content
      (parse (StringBufferInputStream. (fetch-prediction-data-xml route stop-id))))))))

(defstruct prediction :route :direction :eta)

(defn make-prediction [bustracker-prediction]
  (struct-map
      prediction
      :route (:rt bustracker-prediction)
      :direction (:rtdir bustracker-prediction)
      :eta (last (re-seq #"\S+" (:prdtm bustracker-prediction)))))

(defn make-predictions [route stop-id]
  (map
   make-prediction
   (fetch-prediction-data route stop-id)))



