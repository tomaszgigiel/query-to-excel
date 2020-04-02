(ns query-to-excel.core
  (:use query-to-excel.common)
  (:gen-class))

(defn execute [props]
  (let [query (load-query-from-file (props :db.query.file))]
    (with-query (props :db.classforname) (props :db.spec) query (fn [maps] (perform maps (props :workbook.sheet) (props :workbook.file) query))))) 

(defn -main
  "Query result to Excel."
  [& args]

  (let [in (first args)]
    (cond
      (= in nil)(exit 1 (help))
      (> (count args) 1)(exit 1 (help))
      :else (do (execute (load-props in))(println "ok"))))
)
