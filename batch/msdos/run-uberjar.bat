md "%HOMEPATH%\_delete_content\"
pushd %~dp0\..\..
if not exist ".\target\uberjar\query-to-excel-uberjar.jar" (
  rmdir /s /q target
  call lein do clean, uberjar
)
call java -cp .\target\uberjar\query-to-excel-uberjar.jar pl.tomaszgigiel.query_to_excel.core
pause
popd
