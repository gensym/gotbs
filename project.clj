(defproject gotbs "0.0.1"
  :description "CTA Bus Tracker predictions for multi-leg commutes"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2"]
                 [aleph "0.2.1-alpha2-SNAPSHOT"]
                 [org.clojure/core.memoize "0.5.1"]
                 [org.webbitserver/webbit "0.2.0"]
                 [org.eclipse.jetty/jetty-servlet "7.4.2.v20110526"]
		 [ring/ring-core "1.0.1"]
                 [ring/ring-jetty-adapter "1.0.1"]
		 [ring/ring-devel "1.0.1"]
                 [clj-time "0.3.5"]
		 [enlive "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.4.0"]
                     [midje "1.3.2-SNAPSHOT"]
                     [org.gensym/tools.trace "0.1"]]

  :main gotbs.run)
