(ns query-to-excel.common
  (:import java.sql.Statement)
  (:import java.io.File)
  (:import org.apache.commons.io.FileUtils)
  (:import java.sql.Connection)
  (:import java.sql.DriverManager)
  (:import java.sql.ResultSet)
  (:import java.sql.SQLException)
  (:import java.io.FileInputStream)
  (:import java.io.FileOutputStream)
  (:import org.apache.poi.ss.usermodel.Cell)
  (:import org.apache.poi.ss.usermodel.DataFormatter)
  (:import org.apache.poi.ss.usermodel.Row)
  (:import org.apache.poi.ss.usermodel.WorkbookFactory)
  (:import org.apache.poi.ss.usermodel.CellType)
  (:import org.apache.poi.ss.usermodel.CellStyle)
  (:import org.apache.poi.xssf.usermodel.XSSFWorkbook)
  (:import org.apache.poi.xssf.usermodel.XSSFDataFormat)
  (:import org.apache.poi.hssf.usermodel.HSSFDataFormat)
  (:gen-class))

(defn help [] (str "Usage: java -jar query-to-excel-standalone.jar C:\\foo\\my-properties.properties\n"))
(defn exit [status msg] (println msg)(System/exit status))

;https://stackoverflow.com/questions/38283891/how-to-wrap-a-string-in-an-input-stream
(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(defn load-props [path]
  (with-open [in (string->stream (clojure.string/replace (slurp path) "\\" "\\\\"))]
    (let [props (java.util.Properties.)]
      (.load props in)
      (into {} (for [[k v] props] [(keyword k) v])))))

(defn load-query-from-file [path] (slurp path))

(defn create-or-replace-sheet [wb name]
  ;; TODO: why try?
  (try 
    (doall (map (fn [x] (. wb removeSheetAt x)) (filter (fn [x] (>= x 0)) (list (. wb getSheetIndex (. wb getSheet name))))))
    (catch Exception e (str "caught exception: " (.getMessage e))))
  (. wb createSheet name))

(defn create-cell-style-text [wb] (let [cs (. wb createCellStyle)] (. cs setDataFormat (HSSFDataFormat/getBuiltinFormat "text")) cs))

(defn create-or-replace-workbook [file] (if (.exists file) (new XSSFWorkbook (new FileInputStream file)) (new XSSFWorkbook)))

; https://clojuredocs.org/clojure.core/resultset-seq
(defn with-query [class url query f]
  (Class/forName class)
  (let [conn (DriverManager/getConnection url)
        stmt (doto (.createStatement conn ResultSet/TYPE_FORWARD_ONLY ResultSet/CONCUR_READ_ONLY) (.setFetchSize 0))
        rs (.executeQuery stmt query)]
    (try (f (resultset-seq rs))
    (finally (.close stmt) (.close conn)))))

(defn create-cell [i r v cs] (
  let [c (. r createCell i)]
  (. c setCellStyle cs)
  (. c setCellValue (str v))))

(defn create-row [i sheet l cs] (
  let [r (. sheet createRow i)]
  (println i)
  (doall (map-indexed (fn [i x] (create-cell i r x cs)) l))))

(defn perform [ms sheet-name out info] (
  let [file (new File out)
       wb (create-or-replace-workbook file)
       sheet (create-or-replace-sheet wb sheet-name)
       sheet-info (create-or-replace-sheet wb (str sheet-name "-info"))
       cell-style-text (create-cell-style-text wb)]
  (create-row 0 sheet (map (fn [x] (clojure.string/upper-case(subs (str x) 1))) (keys (first ms))) cell-style-text)
  (doall (map-indexed (fn [i m] (create-row (+ i 1) sheet (vals m) cell-style-text)) ms))
  (doall (map-indexed (fn [i line] (create-row i sheet-info (list line) cell-style-text)) (clojure.string/split-lines info)))
  (with-open [fos (new FileOutputStream file)] (. wb write fos))))
