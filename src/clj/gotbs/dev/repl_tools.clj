(ns gotbs.dev.repl-tools
  (:require [gotbs.bootstrap.webapp :as webapp]
            [cljs.repl.browser]))

(comment
  (do
    (def datomic-uri "datomic:free://localhost:4334/gotbs")
    (def conn (datomic.api/connect datomic-uri))
    (def stop (webapp/start-all conn))))


(comment (stop))

(comment (cemerick.piggieback/cljs-repl
          :repl-env (doto (cljs.repl.browser/repl-env :port 9000)
                      cljs.repl/-setup)))
