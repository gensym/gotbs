(defproject gotbs "0.0.1"
  :description "CTA Bus Tracker predictions for multi-leg commutes"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [midje "0.8.0"]
                 [org.signaut/ring-jetty7-adapter "0.3.8"]
                 [ring/ring-jetty-async-adapter "0.3.3-SNAPSHOT"]
		 [ring/ring-core "0.3.8"]
		 [ring/ring-devel "0.3.8"]
		 [enlive "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-run "1.0.0-SNAPSHOT"]]

)
