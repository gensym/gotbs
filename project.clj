(defproject gotbs "0.0.1"
  :description "CTA Bus Tracker predictions for multi-leg commutes"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2"]
                 [midje "1.3.2-SNAPSHOT"]
                 [org.webbitserver/webbit "0.2.0"]
                 [org.eclipse.jetty/jetty-servlet "7.4.2.v20110526"]
                 ;; TODO - ring-jetty-async-adapter is probably not needed
                 ;[ring/ring-jetty-async-adapter "0.3.3-SNAPSHOT"] ;;
		 [ring/ring-core "1.0.1"]
                 [ring/ring-jetty-adapter "1.0.1"]
		 [ring/ring-devel "1.0.1"]
		 [enlive "1.0.0-SNAPSHOT"]
                 ]
  :dev-dependencies [[swank-clojure "1.4.0"]]

  :main gotbs.run)
