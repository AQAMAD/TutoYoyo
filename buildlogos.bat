ECHO OFF
setlocal enableDelayedExpansion
SET PROJECTPATH=C:\Users\Gregoire\AndroidStudioProjects\TutoYoyo
SET SVGSOURCE=%PROJECTPATH%\app\src\main\svg
SET PNGSOURCE=%PROJECTPATH%\app\src\main\png
SET DRAWABLES=%PROJECTPATH%\app\src\main\res\drawable
SET BASE_MENU_SIZE=96
SET BASE_ICON_SIZE=32
SET /A X1_5_ICON_SIZE=%BASE_ICON_SIZE% * 3 / 2
SET /A X2_ICON_SIZE=%BASE_ICON_SIZE% * 2
SET /A X3_ICON_SIZE=%BASE_ICON_SIZE% * 3
SET /A X4_ICON_SIZE=%BASE_ICON_SIZE% * 4
SET BASE_LOGO_SIZE=96
SET /A X1_5_LOGO_SIZE=%BASE_LOGO_SIZE% * 3 / 2
SET /A X2_LOGO_SIZE=%BASE_LOGO_SIZE% * 2
SET /A X3_LOGO_SIZE=%BASE_LOGO_SIZE% * 3
SET /A X4_LOGO_SIZE=%BASE_LOGO_SIZE% * 4

for %%f in (%SVGSOURCE%\*.svg) do (
	echo "Processing svg image : " %%~nf
	rem is it a logo, appicon, drawable or icon
	set filename=%%~nf
	rem echo "Checking test : " !filename! " should test as " !filename:ic_menu=!
	if NOT "!filename!"=="!filename:ic_menu=!" (
		echo "Generating single menu icon : " %%~nf
		convert -background none %SVGSOURCE%\%%~nf.svg -resize %BASE_MENU_SIZE%x%BASE_MENU_SIZE% %DRAWABLES%\%%~nf.png
	) else (
		if NOT "!filename!"=="!filename:ic_=!" (
			echo "Generating 5 drawables icons : " %%~nf
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %BASE_ICON_SIZE%x%BASE_ICON_SIZE% %DRAWABLES%-mdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X1_5_ICON_SIZE%x%X1_5_ICON_SIZE% %DRAWABLES%-hdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X2_ICON_SIZE%x%X2_ICON_SIZE% %DRAWABLES%-xhdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X3_ICON_SIZE%x%X3_ICON_SIZE% %DRAWABLES%-xxhdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X4_ICON_SIZE%x%X4_ICON_SIZE% %DRAWABLES%-xxxhdpi\%%~nf.png
		) else (
			if NOT "!filename!"=="!filename:logo_=!" (
				echo "Generating 5 drawables logos : " %%~nf
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %BASE_LOGO_SIZE%x%BASE_LOGO_SIZE% %DRAWABLES%-mdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X1_5_LOGO_SIZE%x%X1_5_LOGO_SIZE% %DRAWABLES%-hdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X2_LOGO_SIZE%x%X2_LOGO_SIZE% %DRAWABLES%-xhdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X3_LOGO_SIZE%x%X3_LOGO_SIZE% %DRAWABLES%-xxhdpi\%%~nf.png
			convert -background none %SVGSOURCE%\%%~nf.svg -resize %X4_LOGO_SIZE%x%X4_LOGO_SIZE% %DRAWABLES%-xxxhdpi\%%~nf.png
			) else (
				echo "unknown format to apply : " %%~nf
			)
		)
	) 
)

for %%f in (%PNGSOURCE%\*.png) do (
	echo "Processing png image : " %%~nf
	rem is it a logo, appicon, drawable or icon
	set filename=%%~nf
	rem echo "Checking test : " !filename! " should test as " !filename:ic_menu=!
	if NOT "!filename!"=="!filename:ic_menu=!" (
		echo "Generating single menu icon : " %%~nf
		convert -background none %PNGSOURCE%\%%~nf.png -resize %BASE_MENU_SIZE%x%BASE_MENU_SIZE% %DRAWABLES%\%%~nf.png
	) else (
		if NOT "!filename!"=="!filename:ic_=!" (
			echo "Generating 5 drawables icons : " %%~nf
			convert -background none %PNGSOURCE%\%%~nf.png -resize %BASE_ICON_SIZE%x%BASE_ICON_SIZE% %DRAWABLES%-mdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X1_5_ICON_SIZE%x%X1_5_ICON_SIZE% %DRAWABLES%-hdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X2_ICON_SIZE%x%X2_ICON_SIZE% %DRAWABLES%-xhdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X3_ICON_SIZE%x%X3_ICON_SIZE% %DRAWABLES%-xxhdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X4_ICON_SIZE%x%X4_ICON_SIZE% %DRAWABLES%-xxxhdpi\%%~nf.png
		) else (
			if NOT "!filename!"=="!filename:logo_=!" (
				echo "Generating 5 drawables logos : " %%~nf
			convert -background none %PNGSOURCE%\%%~nf.png -resize %BASE_LOGO_SIZE%x%BASE_LOGO_SIZE% %DRAWABLES%-mdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X1_5_LOGO_SIZE%x%X1_5_LOGO_SIZE% %DRAWABLES%-hdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X2_LOGO_SIZE%x%X2_LOGO_SIZE% %DRAWABLES%-xhdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X3_LOGO_SIZE%x%X3_LOGO_SIZE% %DRAWABLES%-xxhdpi\%%~nf.png
			convert -background none %PNGSOURCE%\%%~nf.png -resize %X4_LOGO_SIZE%x%X4_LOGO_SIZE% %DRAWABLES%-xxxhdpi\%%~nf.png
			) else (
				echo "unknown format to apply : " %%~nf
			)
		)
	) 
)



