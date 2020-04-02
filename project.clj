(defproject query-to-excel "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.6"]
                 [net.sourceforge.jtds/jtds "1.3.1"]
                 [com.microsoft.sqlserver/mssql-jdbc "6.4.0.jre8"]
                 [commons-io/commons-io "2.6"]
                 [org.apache.poi/poi "4.0.1"]
                 [org.apache.poi/poi-ooxml "4.0.1"]
                 [com.ibm.informix/jdbc "4.10.10.0"]
                 [org.postgresql/postgresql "42.2.5"]
                 [mysql/mysql-connector-java "8.0.15"]
                 [com.oracle/ojdbc8 "12.2.0.1"]
                 ]
  :main ^:skip-aot query-to-excel.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :jar-name "query-to-excel.jar" :uberjar-name "query-to-excel-standalone.jar"}}
  :jvm-opts ["-Xms2G" "-Xmx2G" "-XX:-UseGCOverheadLimit" "-XX:+UseConcMarkSweepGC"]
  )
