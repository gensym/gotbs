(ns gotbs.core
  (:use gotbs.web.locations)
  (:use ring.middleware.file)
  (:use ring.middleware.file-info))

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
		    :body (do-get req)}
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Welcome")}))

(defn handler [req]
  (router req))

(def app (-> #'handler
	     (wrap-file "resources/web")
	     (wrap-file-info)))
	     

