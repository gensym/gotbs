(use 'clojure.repl)
(use 'clojure.pprint)
(use 'gotbs.util.thread-utils)

(use 'gotbs.bootstrap.webapp :reload-all)
(require ['gotbs.web.route-subscriptions :as 'subscriber] :reload-all)
(require  ['clojure.data.json :as 'json])

(def subscriptions (subscriber/make-subscriptions))
(def webbit (start-webbit 8888 subscriptions))
(def jetty (start-jetty-core-app))
(def stop (start-all))
