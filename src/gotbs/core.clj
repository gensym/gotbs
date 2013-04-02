(ns gotbs.core
  (:use gotbs.web.locations (locations))
  (:use gotbs.web.routes)
  (:use ring.middleware.reload (wrap-reload))
  (:use ring.middleware.params (wrap-params))
  (:use ring.middleware.file)
  (:use ring.middleware.file-info)
  (:require
   [ring.middleware.logger :as logger]
   [gotbs.web.runs :as runs]))

(def db {})

(defn- json-response [content-fn req]
  {:status 200
   :headers {"Content-Type" "application/json" }
   :body (apply str (content-fn (:params req))) }  )



(defn router [db req]
  (condp = (:uri req)
      "/locations" {:status 200
		    :headers {"Content-Type" "text/html"}
		    :body (apply str (locations req))}
      "/routes"    {:status 200
                    :headers {"Content-Type" "text/html"}
                    :body (apply str (routes req))}
      "/runs" {:status 200
               :headers {"Content-Type" "text/html"}
               :body (apply str (runs/runs req))}
      "/routes/available.json" (json-response available-routes req) 
      "/routes/directions.json" (json-response route-directions req)
      "/routes/route-descriptor.json" (json-response route-descriptor req)
      "/routes/waypoints.json" (json-response route-waypoints req)
      "/runs/for_route.json" (json-response (partial runs/for-route db) req)
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body (str "Oops")}))

(defn handler [conn req]
  (router (datomic.api/db conn) req))


(comment       (logger/wrap-with-plaintext-logger))

(defn app [db-connection]
  (-> (wrap-params (partial handler db-connection))
      (wrap-file "resources/web")
      (wrap-file-info)
      (wrap-reload ['gotbs.web.routes])))
