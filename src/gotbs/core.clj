(ns gotbs.core)

(defn router [req]
  (condp = (:uri req)
      "/error"    {:status 404
		   :headers {"Content-Type" "text/html"}
		   :body (str "Error")}
      "/info"     {:status 200
		   :headers {"Content-Type" "text/html"}
		   :body (str "This is Ring on " (:server-name req))}
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Welcome")}))

(defn app [req]
  (router req))
