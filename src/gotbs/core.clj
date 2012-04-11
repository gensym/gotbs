(ns gotbs.core
  (:use gotbs.web.locations (locations))
  (:use gotbs.web.routes)
  (:use ring.middleware.reload (wrap-reload))
  (:use ring.middleware.params (wrap-params))
  (:use ring.middleware.file)
  (:use ring.middleware.file-info))

(defn- json-response [content-fn req]
  {:status 200
   :headers {"Content-Type" "application/json" }
   :body (apply str (content-fn (:params req))) }
  )

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
		    :body (apply str (locations req))}
      "/routes"    {:status 200
                    :headers {"Content-Type" "text/html"}
                    :body (apply str (routes req))}
      "/routes/available.json" (json-response available-routes req) 
      "/routes/directions.json" (json-response route-directions req)
      "/routes/route-descriptor.json" (json-response route-descriptor req)
      "/routes/waypoints.json" (json-response route-waypoints req)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Welcome")}))

(defn handler [req]
  (router req))

(def app (-> (wrap-params handler)
	     (wrap-file "resources/web")
	     (wrap-file-info)
             (wrap-reload ['gotbs.web.routes])))
	     

