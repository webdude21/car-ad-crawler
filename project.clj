(defproject car-ad-crawler "0.1.0-SNAPSHOT"
  :description "A project that will crawl selected car ad sites"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "https://github.com/webdude21"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [enlive "1.1.6"]]
  :main ^:skip-aot car-ad-crawler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
