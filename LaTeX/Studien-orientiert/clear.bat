@echo off
echo Loesche temporaere Dateien in Haupt-Verzeichnis
if exist *.aux del *.aux
if exist *.bbl del *.bbl
if exist *.blg del *.blg
if exist *.log del *.log
if exist *.gz del *.gz
if exist *.toc del *.toc
if exist *.lol del *.lol
if exist *.nav del *.nav
if exist *.snm del *.snm
if exist *.lof del *.lof
if exist *.lot del *.lot
if exist *.loe del *.loe
if exist *.out del *.out
if exist *.bak del *.bak
if exist *.gz(busy) del *.gz(busy)
if exist *.bcf del *.bcf
if exist *.xml del *.xml
if exist *.bib$ del *.bib$