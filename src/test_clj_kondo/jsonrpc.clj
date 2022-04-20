(ns test-clj-kondo.jsonrpc
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [test-clj-kondo.main :as main]))

(defn read-jsonrpc-file [fname & reader-opts]
  (with-open [rdr (apply io/reader fname reader-opts)]
    (loop [data-vec []]
      (let [line (.readLine rdr)]
        (if (nil? line)
          {:error nil, :result :end-of-data, :data data-vec}
          (let [groups (try
                         {:exception nil
                          :ret (re-matches #"^\s*Content-Length:\s*(\d+)\s*$" line)}
                         (catch Exception e
                           {:error :exception-on-re-matches
                            :exception e
                            :string line}))]
            (cond
              (:exception groups) groups
              
              (:ret groups)
              (let [content-len (parse-long (nth (:ret groups) 1))
                    should-be-blank-line (.readLine rdr)
                    buf (char-array content-len)
                    read-len (.read rdr buf 0 content-len)]
                (cond
                  (not (str/blank? should-be-blank-line))
                  {:error :expected-blank-line
                   :line-that-should-be-blank should-be-blank-line
                   :data data-vec}
                  
                  (= read-len content-len)
                  (let [json-str (String. buf)
                        data (try
                               {:exception nil
                                :data (json/read-str json-str)}
                               (catch Exception e
                                 {:exception e
                                  :bad-string json-str}))]
                    (if (nil? (:exception data))
                      (recur (conj data-vec (:data data)))
                      (merge data {:error :json-read-str-failed})))
                  
                  :else
                  {:error :read-failed-to-return-expected-content-length
                   :expected-content-length content-len
                   :actual-read-return read-len
                   :data data-vec}))
              
              :else
              (if (str/blank? line)
                ;; Then perhaps we have reached the normal end of data.
                ;; Would be nice to double-check by reading the rest of
                ;; the input, if any, and confirming it is all white
                ;; space.
                {:error nil
                 :result :end-of-data
                 :data data-vec}
                {:error :expected-content-length
                 :result line
                 :data data-vec}))))))))

(defn parse-args [args]
  (when (not= (count args) 1)
    (main/iprintf *err* "usage: <progname> <jsonrpc-file>\n")
    (System/exit 1))
  {:filename (nth args 0)})

(defn -main [& args]
  (let [opts (parse-args args)
        dat (read-jsonrpc-file (:filename opts))]
    (when (:error dat)
      (pp/pprint dat)
      (System/exit 1))
    (pp/pprint (:data dat))
    (System/exit 0)))


(comment


(do
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])
(require '[clojure.data.json :as json])
)

(def fname "clojure-lsp-interaction-on-test-clj-kondo1/clojure-lsp-stdin.txt")
(def fname "clojure-lsp-interaction-on-test-clj-kondo1/clojure-lsp-stdout.txt")
;;(def r1 (java.io.PushbackReader. (io/reader fname)))
(def d1 (read-jsonrpc-file fname))
(class d1)
(count d1)
(keys d1)
(:error d1)
(:result d1)
(:string d1)
(count (:data d1))
(pp/pprint (:data d1))
(def e1 *e)
e1

(def r1 (io/reader fname))

(def l1 (.readLine r1))
l1
(doc re-matches)
(def m1 (re-matches #"^\s*Content-Length:\s*(\d+)\s*$" l1))
(def len1 (parse-long (nth m1 1)))
len1
(def l2 (.readLine r1))
l2
(doc char-array)
(def c1 (char-array len1))
(alength c1)
(def ret1 (.read r1 c1 0 len1))
ret1
(str c1)
(def l2 (String. c1))
l2

(def j1 (json/read-str l2))
j1
(pprint j1)

)
