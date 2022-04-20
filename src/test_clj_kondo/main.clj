(ns test-clj-kondo.main
  (:import (java.net URLEncoder))
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.set :as set]
            [clojure.java.javadoc]
            [clojure.java.io :as io]
            [clojure data pprint repl set string xml zip]
            clojure.edn
            clojure.instant
            clojure.main
            clojure.stacktrace
            clojure.template
            clojure.test
            clojure.uuid))

(def ^:dynamic *auto-flush* true)

(defn printf-to-writer [w fmt-str & args]
  (binding [*out* w]
    (apply clojure.core/printf fmt-str args)
    (when *auto-flush* (flush))))

(defn iprintf [fmt-str-or-writer & args]
  (if (instance? CharSequence fmt-str-or-writer)
    (apply printf-to-writer *out* fmt-str-or-writer args)
    (apply printf-to-writer fmt-str-or-writer args)))

(defn die [fmt-str & args]
  (apply iprintf *err* fmt-str args)
  (System/exit 1))


(defn read-edn-safely [x & opts]
  (with-open [r (java.io.PushbackReader. (apply io/reader x opts))]
    (clojure.edn/read r)))

(defmacro verify [cond]
  `(when (not ~cond)
     (iprintf "%s\n" (str "verify of this condition failed: " '~cond))
     (throw (Exception.))))

(defn parse-args [args]
  (let [filename (if (not= (count args) 1)
                   nil
                   (nth args 0))]
    {:filename filename}))

(defn read-edn-file [fname]
  (let [dat (cond
              (nil? fname) :no-filename
              (str/ends-with? fname ".edn") (read-edn-safely fname)
              :else :unknown-filename-suffix)]
    (cond
      (= dat :no-filename)
      (iprintf *err* "No file specified.\n")

      (= dat :unknown-filename-suffix)
      (iprintf *err* "File name '%s' has unknown suffix" fname))
    dat))


(defn -main [& args]
  (let [opts (parse-args args)
        dat (read-edn-file (:filename opts))]
    (when-not (#{:no-filename :unknown-filename-suffix} dat)
      (iprintf "Read edn file with return type %s from file '%s'\n"
               (class dat)
               (:filename opts)))))
