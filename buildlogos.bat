SET PROJECTPATH=C:\Users\Gregoire\AndroidStudioProjects\TutoYoyo
SET SVGSOURCE=%PROJECTPATH%\app\src\main\svg
SET DRAWABLES=%PROJECTPATH%\app\src\main\res\drawable-
for %%f in (%SVGSOURCE%\*.svg) do (
	echo %%~nf
	convert -background none %SVGSOURCE%\%%~nf.svg -resize 32x32 %DRAWABLES%mdpi\%%~nf.png
	convert -background none %SVGSOURCE%\%%~nf.svg -resize 48x48 %DRAWABLES%hdpi\%%~nf.png
	convert -background none %SVGSOURCE%\%%~nf.svg -resize 64x64 %DRAWABLES%xhdpi\%%~nf.png
	convert -background none %SVGSOURCE%\%%~nf.svg -resize 96x96 %DRAWABLES%xxhdpi\%%~nf.png
	convert -background none %SVGSOURCE%\%%~nf.svg -resize 128x128 %DRAWABLES%xxxhdpi\%%~nf.png
)


