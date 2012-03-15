(use 'clojure.repl)
(use 'clojure.pprint)
(use 'gotbs.util.thread-utils)

(use 'gotbs.bootstrap.webapp :reload-all)
(require ['gotbs.web.route-subscriptions :as 'subscriber] :reload-all)
(require ['gotbs.feed.subscriptions :as 'feed] :reload-all)
(require  ['clojure.data.json :as 'json])

(def feed-subscriptions (feed/make-subscriptions))
(def subscriptions (subscriber/make-subscriptions feed-subscriptions))
(def webbit (start-webbit 8888 subscriptions))



(def jetty (start-jetty-core-app))
(def stop (start-all))
