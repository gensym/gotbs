(ns gotbs.web.locations)

(defn do-get [req]
  (str "Hello world: " (:uri req) " " (:query-string req)))