(ns car-ad-crawler.core
  (:require
    [car-ad-crawler.autobg :as auto-bg])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (auto-bg/print-cars))
