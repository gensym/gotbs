(ns gotbs.core
  (:use gotbs.web.locations)
  (:use ring.middleware.file)
  (:use ring.middleware.file-info)
  (:use net.cgrand.enlive-html))

(deftemplate locations-old "web/index.html" [a]
  [:div#main] (content a))

(def *dummy-context*
     [{
       :route "72 North"
       :direction "Westbound"
       :eta "4:20"
       }
      {
       :route "50 Armitage"
       :direction "Westbound"
       :eta "4:25"
       }])

(defsnippet locations-model "web/locations.html" 
  [:#locations [:.location (nth-of-type 1)]]
  [{:keys [route direction eta]}]
  [:.route-name] (content route)
  [:.route-direction] (content direction)
  [:.prediction-time] (content eta))

(deftemplate locations "web/index.html"
  [locations-data]
  [:div#main] (content (map locations-model locations-data)))

(defn router [req]
  (condp = (:uri req)
      "/error"    {:status 404
		   :headers {"Content-Type" "text/html"}
		   :body (str "Error")}
      "/info"     {:status 200
		   :headers {"Content-Type" "text/html"}
		   :body (str "This is Ring on " (:server-name req))}
      "/locations" {:status 200
		    :headers {"Content-Type" "text/html"}
		    :body (apply str (locations *dummy-context*))}
      "/locations2" {:status 200
		    :headers {"Content-Type" "text/html"}
		    :body (do-get req)}
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Welcome")}))

(defn handler [req]
  (router req))
					; (ns x (:use net.cgrand.enlive-html))
					; (html-resource "web/index.html")

(def app (-> #'handler
	     (wrap-file "resources/web")
	     (wrap-file-info)))
	     

