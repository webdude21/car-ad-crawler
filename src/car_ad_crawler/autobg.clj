(ns car-ad-crawler.autobg
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(def ^:dynamic *base-url* "https://www.auto.bg/pcgi/results.cgi?cat=1&sort=3&marka=Alfa+Romeo&model=159+sportwagon")

(def ^:dynamic *cars-ads-selector*
  [html/root :#rightColumn :> :div.results :> :.resultItem :> :ul :> :li])

(def ^:dynamic *car-headline-selector*
  [html/root :> :.head])

(def ^:dynamic *encoding* "windows-1251")

(def ^:dynamic *car-description-selector* [html/root :> :.info :> :div])

(def ^:dynamic *car-summary-selector* [html/root :> :.info :> :span])

(defn split-on-space [word]
  "Splits a string on words"
  (clojure.string/split word #"\s+"))

(defn squish [line]
  (str/triml (str/join " "
                       (split-on-space (str/replace line #"\n" " ")))))

(defn fetch-url [url encoding]
  (-> url java.net.URL.
      .getContent (java.io.InputStreamReader. encoding)
      html/html-resource))

(defn cars []
  (html/select (fetch-url *base-url* *encoding*) *cars-ads-selector*))

(defn tokenize [car-info]
  (if (nil? car-info)
    nil
    (zipmap [:first-registered
             :odometer-reading
             :gearbox-type
             :fuel-type
             :power] (map str/trim (str/split car-info #"\|")))))

(defn extract [node]
  (let [car-headline (first (html/select [node] *car-headline-selector*))
        car-info (tokenize (html/text (first (html/select [node] *car-description-selector*))))
        car-summary (first (html/select [node] *car-summary-selector*))
        result (vec (conj (map squish (map html/text [car-headline car-summary])) car-info))]
    (zipmap [:headline :info :summary] result)))

(defn empty-car-listing? [node]
  (some (fn [[_ v]] (= v "")) node))

(defn check [story key default]
  (let [v (key story)]
    (if (not= v "") v default)))

(defn print-car [story]
  (println)
  (println (check story :headline "No headline"))
  (println "\t" (check story :info "No info"))
  (println "\t" (check story :summary "No summary")))

(defn print-cars []
  (doseq [car (remove empty-car-listing? (map extract (cars)))]
    (print-car car)))