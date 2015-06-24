(defproject clojure-chat "0.1.0-SNAPSHOT"
  :description "Demo chat application for Semaphore Community article, How to set up a Clojure environment with Ansible"
  :url "https://github.com/kendru/clojure-chat"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.novemberain/monger "2.0.0"]
                 [com.novemberain/langohr "3.0.1"]
                 [http-kit "2.1.18"]
                 [environ "1.0.0"]
                 [bidi "1.19.1"]
                 [cheshire "5.5.0"]]

  :main clojure-chat.main

  :profiles {:uberjar {:aot :all}})
