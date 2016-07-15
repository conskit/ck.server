(defproject ck.server "0.1.0-SNAPSHOT"
  :description "Server module for Conskit"
  :url "https://github.com/conskit/ck.server"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [conskit "0.1.0-SNAPSHOT"]]
  :profiles {:dev {:source-paths ["dev" "http-kit"]
                   :dependencies [[puppetlabs/trapperkeeper "1.3.0" :classifier "test"]
                                  [puppetlabs/kitchensink "1.3.0" :classifier "test" :scope "test"]
                                  [midje "1.8.3"]
                                  [http-kit "2.1.18"]]
                   :plugins [[lein-midje "3.2"]]}
             :http-kit-server {:source-paths ["http-kit"]
                               :dependencies [[http-kit "2.1.18"]]}}
  :classifiers {:http-kit :http-kit-server})
