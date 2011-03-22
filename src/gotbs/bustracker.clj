(ns gotbs.bustracker
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream))
  (:use
   clojure.xml (parse)
   clojure.java.io (input-stream)
   clojure.contrib.str-utils (re-gsub)))

;; BEGIN HARDCODING
(def api-key (System/getenv "CTA_BUSTRACKER_API_KEY"))

(def west-bound "West Bound")

(def north-bound "North Bound")

(def milwaukee-and-north-avenue-damen-56 "15847")

(def milwaukee-and-talman-56 "5552")

(def madison-and-clinton-56 "455")

(def destination "Jefferson Park Blue Line")
;; END HARDCODING

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
	(apply str (line-seq buf))))))

(defn fetch-routes-data-xml []
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getroutes?key=" api-key)))

(defn fetch-location-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&rt=" route)))

(defn fetch-prediction-data-xml [route stop-id]
  (fetch-url
   (str
    "http://www.ctabustracker.com/bustime/api/v1/getpredictions?key=" api-key
    "&rt=" route
    "&stpid=" stop-id)))

(defn fetch-stop-data-xml [route direction]
  (let [dir (re-gsub #"\s" "+" direction)]
    (fetch-url
     (str "http://www.ctabustracker.com/bustime/api/v1/getstops?key=" api-key "&rt=" route "&dir=" dir))))

(defn fetch-vehicles-on-route-data-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&rt=" route)))

(defn fetch-vehicles-data-xml [vehicles]
  "Takes a sequence of vehicles (limit 10) and gets their information"
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&vid=" (reduce #(str %1 "," %2) vehicles))))

(defn fetch-pattern-data-for-route-xml [route]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getpatterns?key=" api-key "&rt=" route)))

(defn fetch-pattern-data-by-id-xml [pattern-id]
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getpatterns?key=" api-key "&pid=" pattern-id)))


(defn content-xml-to-map [xml]
  "Takes a seq in the form of [{:tag :a :content [\"something\"]} {:tag b :content [\"else\"]}] and turns it into a map, with the value of the :tag as the keys and the values of :content as values. If the value of :content is a list, takes the first item off of that list."
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

(defn remove-tag [tag-name s]
  (remove #(= (:tag %) tag-name) s))

(defn fetch-stop-data [route direction]
  (map
   content-xml-to-map
   (map
    :content 
    (filter-tag
     :stop
     (:content
      (parse
       (StringBufferInputStream.
	(fetch-stop-data-xml route direction))))))))

(defn stop-id [route direction stop-name]
  (first (map :stpid (filter #(= (:stpnm %1) stop-name) (fetch-stop-data route direction)))))

(defn stop-name [route direction stop-id]
     (first (map :stpnm (filter #(= (:stpid %) stop-id) (fetch-stop-data route direction)))))

(let [construct-vehicle-data
      (fn [vehicle-data-xml]
	(map
	 content-xml-to-map
	 (map
	  :content
	  (filter-tag
	   :vehicle
	   (:content
	    (parse (StringBufferInputStream. vehicle-data-xml)))))))]
  (defn fetch-vehicles-on-route-data [route]
    (construct-vehicle-data (fetch-vehicles-on-route-data-xml route)))

  (defn fetch-vehicles-data [& vehicle_ids]
    (construct-vehicle-data (fetch-vehicles-data-xml vehicle_ids))))

;; Milwaukee PIDs - 1970, 1971

(defn fetch-pattern-data-by-id [pattern-id]
  "Fetch data about the pattern by the pattern ID. Excludes waypoints"
  (content-xml-to-map
   (remove-tag
    :pt
    (first
     (map
      :content
      (filter-tag
       :ptr
       (:content
        (parse (StringBufferInputStream. (fetch-pattern-data-by-id-xml pattern-id))))))))))

(defn fetch-pattern-data-for-route [route dir]
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
	  (parse (StringBufferInputStream. (fetch-pattern-data-for-route-xml route))))))))))))

(defn fetch-routes []
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :route
     (:content
      (parse (StringBufferInputStream. 
              (fetch-routes-data-xml))))))))

(defn destination [route dir]
  (:stpnm (last (fetch-pattern-data-for-route route dir))))

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

